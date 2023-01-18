package com.vetka.gateway.persistence.api;

import com.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;

public interface IPersistenceServiceFacade {

    IGraphQlEndpointService graphQlEndpointService();
}
