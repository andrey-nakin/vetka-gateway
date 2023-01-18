package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationErrors;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationPayload;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationResponse;
import com.vetka.gateway.mgmt.service.FederationService;
import com.vetka.gateway.persistence.api.PersistenceServiceFacade;
import com.vetka.gateway.persistence.api.exception.endpoint.DuplicatingEndpointNameException;
import java.util.List;
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
    private final FederationService federationService;

    @MutationMapping
    public Mono<GraphQlEndpointCreationPayload> createGraphQlEndpoint(
            @NonNull @Argument final GraphQlEndpointCreationInput input) {

        log.info("createGraphQlEndpoint input={}", input);

        return persistenceServiceFacade.serviceFacade()
                .graphQlEndpointService()
                .create(input)
                .flatMap(federationService::reconfigure)
                .map(e -> (GraphQlEndpointCreationPayload) GraphQlEndpointCreationResponse.builder()
                        .graphQlEndpoint(e)
                        .build())
                .onErrorResume(DuplicatingEndpointNameException.class, ex -> Mono.just(
                        GraphQlEndpointCreationErrors.builder()
                                .errors(List.of(EndpointErrorDuplicatingName.builder()
                                        .message("There is already an endpoint with the given name")
                                        .name(ex.getName())
                                        .build()))
                                .build()));
    }
}
