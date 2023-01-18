package com.vetka.gateway.persistence.api.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGraphQlEndpointService {

    Flux<GraphQlEndpoint> findAll();

    Mono<GraphQlEndpoint> create(GraphQlEndpointCreationInput input);

    Mono<Void> delete(String id) throws EndpointNotFoundException;
}
