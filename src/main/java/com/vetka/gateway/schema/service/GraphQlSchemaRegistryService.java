package com.vetka.gateway.schema.service;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.schema.bo.GraphQlSchemaInfo;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.schema.exception.SchemaConflictException;
import graphql.schema.AsyncDataFetcher;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphQlSchemaRegistryService {

    private final IPersistenceServiceFacade persistenceServiceFacade;

    @Getter
    private volatile GraphQlSchemaInfo info;

    public void reloadSchemas() {
        final var endpoints = reloadAndParse();
        this.info = new GraphQlSchemaInfo(buildSchema(endpoints), endpoints);
        log.info("endpoints loaded: {}", endpoints.size());
    }

    @PostConstruct
    void init() {
        reloadSchemas();
    }

    private List<GraphQlEndpointInfo> reloadAndParse() {
        final var result = persistenceServiceFacade.graphQlEndpointService()
                .findAll()
                .map(this::parse)
                .collectList()
                .defaultIfEmpty(List.of())
                .block();
        checkConflicts(result);
        return result;
    }

    private GraphQlEndpointInfo parse(@NonNull final GraphQlEndpoint graphQlEndpoint) {
        log.info("parsing graphQlEndpoint={}", graphQlEndpoint);

        final var schema = SchemaGenerator.createdMockedSchema(graphQlEndpoint.getSchema());
        final var result = GraphQlEndpointInfo.builder()
                .graphQlEndpoint(graphQlEndpoint)
                .schema(schema)
                .queries(fieldNames(schema.getQueryType()))
                .mutations(fieldNames(schema.getMutationType()))
                .subscriptions(fieldNames(schema.getSubscriptionType()))
                .build();
        log.info("parsed {}", result);
        return result;
    }

    private static Set<String> fieldNames(@Nullable final GraphQLObjectType src) {
        return Optional.ofNullable(src)
                .map(t -> t.getFields().stream().map(GraphQLFieldDefinition::getName).collect(Collectors.toSet()))
                .orElse(Set.of());
    }

    private void checkConflicts(@NonNull final List<GraphQlEndpointInfo> endpoints) {
        final var sb = new StringBuilder();
        var ok = true;

        for (int i = 0; i < endpoints.size(); i++) {
            final var a = endpoints.get(i);
            for (int j = i + 1; j < endpoints.size(); j++) {
                final var b = endpoints.get(j);

                ok = ok && checkIntersections(sb, a, b, GraphQlEndpointInfo::getQueries, "queries");
                ok = ok && checkIntersections(sb, a, b, GraphQlEndpointInfo::getMutations, "mutations");
                ok = ok && checkIntersections(sb, a, b, GraphQlEndpointInfo::getSubscriptions, "subscriptions");
            }
        }

        if (!ok) {
            throw new SchemaConflictException(sb.toString());
        }
    }

    private static boolean checkIntersections(@NonNull final StringBuilder sb, @NonNull final GraphQlEndpointInfo a,
            @NonNull final GraphQlEndpointInfo b, @NonNull final Function<GraphQlEndpointInfo, Set<String>> getter,
            @NonNull final String typeName) {

        final var intersections = intersection(getter.apply(a), getter.apply(b));
        if (!intersections.isEmpty()) {
            sb.append("There are conflicting ")
                    .append(typeName)
                    .append(" in endpoints ")
                    .append(a.getGraphQlEndpoint().getName())
                    .append(" and ")
                    .append(b.getGraphQlEndpoint().getName())
                    .append(": ")
                    .append(intersections)
                    .append(".\n");
            return false;
        } else {
            return true;
        }
    }

    private static <T> Set<T> intersection(@NonNull final Set<T> a, @NonNull final Set<T> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    private GraphQLSchema buildSchema(final List<GraphQlEndpointInfo> endpoints) {
        final var typeDefinitionRegistry = new TypeDefinitionRegistry();
        final Map<String, DataFetcher> queryDataFetchers = new HashMap<>();
        final Map<String, DataFetcher> mutationDataFetchers = new HashMap<>();
        final Map<String, DataFetcher> subscriptionDataFetchers = new HashMap<>();

        for (final var ep : endpoints) {
            final var schemaParser = new SchemaParser();
            final var epTdr = schemaParser.parse(ep.getGraphQlEndpoint().getSchema());
            typeDefinitionRegistry.merge(epTdr);

            ep.getQueries()
                    .forEach(fieldName -> queryDataFetchers.put(fieldName, new GraphQlQueryDataFetcher(fieldName, ep)));
            ep.getMutations()
                    .forEach(fieldName -> mutationDataFetchers.put(fieldName,
                            new GraphQlMutationDataFetcher(fieldName, ep)));
            ep.getSubscriptions()
                    .forEach(fieldName -> subscriptionDataFetchers.put(fieldName,
                            new GraphQlSubscriptionDataFetcher(fieldName, ep)));
            var adf = new AsyncDataFetcher<>(new GraphQlSubscriptionDataFetcher("countries", ep));
        }

        final var runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetchers(queryDataFetchers))
                .type("Mutation", builder -> builder.dataFetchers(mutationDataFetchers))
                .type("Subscription", builder -> builder.dataFetchers(subscriptionDataFetchers))
                .build();

        final var schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    }
}
