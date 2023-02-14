package com.vetka.gateway.persistence.api.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateInput;
import java.util.Set;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGraphQlEndpointService {

    Flux<GraphQlEndpoint> findAll();

    Mono<GraphQlEndpoint> create(GraphQlEndpointCreationInput input);

    Mono<GraphQlEndpoint> update(GraphQlEndpointUpdateInput input, Set<String> updatableFields);

    Mono<Void> delete(String id);
}
