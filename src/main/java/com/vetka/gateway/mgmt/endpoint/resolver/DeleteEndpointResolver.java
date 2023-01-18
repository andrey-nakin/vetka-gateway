package com.vetka.gateway.mgmt.endpoint.resolver;

import com.vetka.gateway.mgmt.endpoint.model.EndpointDeletionErrors;
import com.vetka.gateway.mgmt.endpoint.model.EndpointDeletionPayload;
import com.vetka.gateway.mgmt.endpoint.model.EndpointDeletionResponse;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorUnknownId;
import com.vetka.gateway.persistence.api.PersistenceServiceFacade;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
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
public class DeleteEndpointResolver {

    private final PersistenceServiceFacade persistenceServiceFacade;

    @MutationMapping
    public Mono<EndpointDeletionPayload> deleteEndpoint(@NonNull @Argument final String id) {
        log.info("deleteEndpoint id={}", id);

        return persistenceServiceFacade.serviceFacade()
                .graphQlEndpointService()
                .delete(id)
                .thenReturn((EndpointDeletionPayload) EndpointDeletionResponse.builder().id(id).build())
                .onErrorReturn(EndpointNotFoundException.class, EndpointDeletionErrors.builder()
                        .errors(List.of(EndpointErrorUnknownId.builder()
                                .message("There is no endpoint with the given ID")
                                .id(id)
                                .build()))
                        .build());
    }
}
