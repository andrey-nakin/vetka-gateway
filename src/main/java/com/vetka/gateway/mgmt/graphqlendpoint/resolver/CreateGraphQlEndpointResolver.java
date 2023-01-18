package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationPayload;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationResponse;
import com.vetka.gateway.persistence.api.PersistenceServiceFacade;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CreateGraphQlEndpointResolver {

    private final PersistenceServiceFacade persistenceServiceFacade;

    @MutationMapping
    public Mono<GraphQlEndpointCreationPayload> createGraphQlEndpoint(
            @NonNull @Argument final GraphQlEndpointCreationInput input) {

        log.info("createGraphQlEndpoint input={}", input);

        return persistenceServiceFacade.serviceFacade()
                .graphQlEndpointService()
                .create(input)
                .map(e -> GraphQlEndpointCreationResponse.builder().graphQlEndpoint(e).build());
    }
}
