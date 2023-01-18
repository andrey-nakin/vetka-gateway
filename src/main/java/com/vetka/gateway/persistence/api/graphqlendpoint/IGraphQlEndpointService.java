package com.vetka.gateway.persistence.api.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import reactor.core.publisher.Flux;

public interface IGraphQlEndpointService {

    Flux<GraphQlEndpoint> findAll();
}
