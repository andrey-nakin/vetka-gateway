package com.vetka.gateway.persistence.api.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGraphQlEndpointService {

    Flux<GraphQlEndpoint> findAll();

    Mono<GraphQlEndpoint> create(Map<String, Object> input);

    Mono<GraphQlEndpoint> update(Map<String, Object> input);

    Mono<Void> delete(String id);
}
