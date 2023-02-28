package io.vetka.gateway.persistence.mongo.service;

import io.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import io.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import io.vetka.gateway.persistence.mongo.service.graphqlendpoint.MongoGraphQlEndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class MongoPersistenceServiceFacade implements IPersistenceServiceFacade {

    private final MongoGraphQlEndpointService mongoGraphQlEndpointService;

    public IGraphQlEndpointService graphQlEndpointService() {
        return mongoGraphQlEndpointService;
    }
}
