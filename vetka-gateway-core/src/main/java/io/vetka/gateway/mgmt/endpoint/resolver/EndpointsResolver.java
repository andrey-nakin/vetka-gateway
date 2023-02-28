package io.vetka.gateway.mgmt.endpoint.resolver;

import io.vetka.gateway.mgmt.endpoint.model.IEndpoint;
import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class EndpointsResolver {

    private final IGraphQlEndpointService graphQlEndpointService;

    @QueryMapping
    public Flux<IEndpoint> endpoints() {
        return graphQlEndpointService.findAll().map(e -> e);
    }
}
