package com.vetka.gateway.schema.service;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.idl.SchemaGenerator;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private volatile List<GraphQlEndpointInfo> endpoints;

    public void reloadSchemas() {
        final var result = persistenceServiceFacade.graphQlEndpointService()
                .findAll()
                .map(this::parse)
                .collectList()
                .defaultIfEmpty(List.of())
                .block();
        this.endpoints = result;
        log.info("endpoints loaded: {}", result.size());
    }

    @PostConstruct
    void init() {
        reloadSchemas();
    }

    private GraphQlEndpointInfo parse(@NonNull final GraphQlEndpoint graphQlEndpoint) {
        log.info("parsing graphQlEndpoint={}", graphQlEndpoint);

        final var schema = SchemaGenerator.createdMockedSchema(graphQlEndpoint.getSchema());
        final var result = GraphQlEndpointInfo.builder()
                .graphQlEndpoint(graphQlEndpoint)
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
}
