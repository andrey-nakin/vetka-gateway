package io.vetka.gateway.mgmt.endpoint.resolver;

import io.vetka.gateway.mgmt.endpoint.model.EndpointDeletionErrors;
import io.vetka.gateway.mgmt.endpoint.model.EndpointDeletionPayload;
import io.vetka.gateway.mgmt.endpoint.model.EndpointDeletionResponse;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorUnknownId;
import io.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
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

    private final IPersistenceServiceFacade persistenceServiceFacade;
    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    @MutationMapping
    public Mono<EndpointDeletionPayload> deleteEndpoint(@NonNull @Argument final String id) {
        log.info("deleteEndpoint id={}", id);

        return persistenceServiceFacade.graphQlEndpointService()
                .delete(id)
                .doOnSuccess(unused -> graphQlSchemaRegistryService.reloadSchemas())
                .thenReturn((EndpointDeletionPayload) EndpointDeletionResponse.builder().id(id).build())
                .onErrorResume(EndpointNotFoundException.class, ex -> Mono.just(EndpointDeletionErrors.builder()
                        .errors(List.of(EndpointErrorUnknownId.builder()
                                .message("There is no endpoint with the given ID")
                                .id(ex.getId())
                                .build()))
                        .build()));
    }
}
