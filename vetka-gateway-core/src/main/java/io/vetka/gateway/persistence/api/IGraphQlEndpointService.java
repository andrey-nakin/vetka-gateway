package io.vetka.gateway.persistence.api;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IGraphQlEndpointService {

    Flux<GraphQlEndpoint> findAll();

    /**
     * Can return {@link io.vetka.gateway.persistence.exception.endpoint.EndpointDuplicatingNameException}.
     */
    Mono<GraphQlEndpoint> create(Map<String, Object> input);

    /**
     * Can return:
     * <ul>
     *     <li>{@link io.vetka.gateway.persistence.exception.endpoint.EndpointNotFoundException}</li>
     *     <li>{@link io.vetka.gateway.persistence.exception.endpoint.EndpointDuplicatingNameException}</li>
     * </ul>
     */
    Mono<GraphQlEndpoint> update(Map<String, Object> input);

    /**
     * Can return {@link io.vetka.gateway.persistence.exception.endpoint.EndpointNotFoundException}.
     */
    Mono<Void> delete(String id);
}
