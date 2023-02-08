package com.vetka.gateway.schema.service;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import jakarta.annotation.PostConstruct;
import java.util.List;
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
        log.info("parse graphQlEndpoint={}", graphQlEndpoint);
        return GraphQlEndpointInfo.builder().graphQlEndpoint(graphQlEndpoint).build();
    }
}
