package io.vetka.gateway.persistence.api;

import io.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;

public interface IPersistenceServiceFacade {

    IGraphQlEndpointService graphQlEndpointService();
}
