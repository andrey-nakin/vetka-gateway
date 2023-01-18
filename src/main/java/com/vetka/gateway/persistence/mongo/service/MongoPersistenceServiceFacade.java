package com.vetka.gateway.persistence.mongo.service;

import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import com.vetka.gateway.persistence.mongo.service.graphqlendpoint.MongoGraphQlEndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MongoPersistenceServiceFacade implements IPersistenceServiceFacade {

    private final MongoGraphQlEndpointService mongoGraphQlEndpointService;

    public IGraphQlEndpointService graphQlEndpointService() {
        return mongoGraphQlEndpointService;
    }
}
