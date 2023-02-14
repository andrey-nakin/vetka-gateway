package com.vetka.gateway.schema.service;

import com.vetka.gateway.graphql.GraphQlSchemaMerger;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.schema.bo.GraphQlSchemaInfo;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.schema.exception.BadSchemaException;
import com.vetka.gateway.schema.exception.SchemaConflictException;
import com.vetka.gateway.transport.api.ITransportService;
import graphql.TypeResolutionEnvironment;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.errors.SchemaProblem;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphQlSchemaRegistryService {

    private final IPersistenceServiceFacade persistenceServiceFacade;
    private final ITransportService transportService;

    @Getter
    private volatile GraphQlSchemaInfo info;

    public Mono<GraphQlSchemaInfo> reloadSchemas() {
        return reloadAndParse().map(endpoints -> {
            final var result = info = new GraphQlSchemaInfo(buildSchema(endpoints), endpoints);
            log.info("endpoints loaded: {}", endpoints.size());
            return result;
        });
    }

    public Mono<String> validateNewSchema(@NonNull final String sdl) {
        return validate(loadAndParse().map(ep -> ep.getGraphQlEndpoint().getSchema()).collectList().map(c -> {
            final var result = new ArrayList<>(c);
            result.add(sdl);
            return result;
        }), sdl);
    }

    public Mono<String> validateExistingSchema(@NonNull final String endpointId, @NonNull final String newSdl) {
        return validate(loadAndParse().map(ep -> {
            if (Objects.equals(endpointId, ep.getGraphQlEndpoint().getId())) {
                // substitute the schema in existing endpoint
                return ep.toBuilder()
                        .graphQlEndpoint(ep.getGraphQlEndpoint().toBuilder().schema(newSdl).build())
                        .build();
            } else {
                return ep;
            }
        }).map(ep -> ep.getGraphQlEndpoint().getSchema()).collectList(), newSdl);
    }

    @PostConstruct
    void init() {
        reloadSchemas().block();
    }

    private Mono<String> validate(@NonNull final Mono<List<String>> sdls, @NonNull final String sdlToCheck) {
        return sdls.flatMap(sdlList -> {
            try {
                GraphQlSchemaMerger.merge(sdlList.stream());
                return Mono.just(sdlToCheck);
            } catch (SchemaProblem ex) {
                return Mono.error(new BadSchemaException(ex.getMessage(), ex, sdlToCheck, ex.getErrors()));
            }
        });
    }

    private Flux<GraphQlEndpointInfo> loadAndParse() {
        return persistenceServiceFacade.graphQlEndpointService().findAll().map(this::parse);
    }

    private Mono<List<GraphQlEndpointInfo>> reloadAndParse() {
        return loadAndParse().collectList().defaultIfEmpty(List.of());
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
        if (endpoints.isEmpty()) {
            return null;
        }

        final var typeDefinitionRegistry = GraphQlSchemaMerger.merge(
                endpoints.stream().map(GraphQlEndpointInfo::getGraphQlEndpoint).map(GraphQlEndpoint::getSchema));

        final Map<String, DataFetcher> queryDataFetchers = new HashMap<>();
        final Map<String, DataFetcher> mutationDataFetchers = new HashMap<>();
        final Map<String, DataFetcher> subscriptionDataFetchers = new HashMap<>();

        for (final var ep : endpoints) {
            final var dataFetcher = new GraphQlDataFetcher(transportService, ep);
            ep.getQueries().forEach(fieldName -> queryDataFetchers.put(fieldName, dataFetcher));
            ep.getMutations().forEach(fieldName -> mutationDataFetchers.put(fieldName, dataFetcher));
            ep.getSubscriptions().forEach(fieldName -> subscriptionDataFetchers.put(fieldName, dataFetcher));
        }

        TypeResolver t = new TypeResolver() {
            @Override
            public GraphQLObjectType getType(TypeResolutionEnvironment env) {
                Object javaObject = env.getObject();
                if (javaObject instanceof Map<?, ?>) {
                    return env.getSchema().getObjectType((String) ((Map) javaObject).get("__typename"));
                } else {
                    throw new IllegalArgumentException("Unsupported java type: " + javaObject.getClass().getName());
                }
            }
        };

        final var runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type(GraphQlConstants.TYPE_QUERY, builder -> builder.dataFetchers(queryDataFetchers))
                .type(GraphQlConstants.TYPE_MUTATION, builder -> builder.dataFetchers(mutationDataFetchers))
                .type(GraphQlConstants.TYPE_SUBSCRIPTION, builder -> builder.dataFetchers(subscriptionDataFetchers))
                .type("Node", typeWriting -> typeWriting.typeResolver(t))
                .build();

        final var schemaGenerator = new SchemaGenerator();
        final var result = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
        log.debug("Superschema\n{}", new SchemaPrinter().print(result));
        return result;
    }
}
