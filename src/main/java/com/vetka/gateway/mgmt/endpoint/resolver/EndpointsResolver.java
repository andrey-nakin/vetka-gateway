package com.vetka.gateway.mgmt.endpoint.resolver;

import com.vetka.gateway.mgmt.endpoint.model.IEndpoint;
import com.vetka.gateway.persistence.api.PersistenceServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class EndpointsResolver {

    private final PersistenceServiceFacade persistenceServiceFacade;

    @QueryMapping
    public Flux<IEndpoint> endpoints() {
        return persistenceServiceFacade.serviceFacade().graphQlEndpointService().findAll().map(e -> e);
    }
}
