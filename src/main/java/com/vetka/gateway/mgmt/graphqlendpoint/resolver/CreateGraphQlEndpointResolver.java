package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorEmptyName;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationErrors;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationPayload;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationResponse;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointErrorBadSchema;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointDuplicatingNameException;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointEmptyNameException;
import com.vetka.gateway.schema.exception.BadSchemaException;
import com.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CreateGraphQlEndpointResolver {

    private final IPersistenceServiceFacade persistenceServiceFacade;
    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    @MutationMapping
    public Mono<GraphQlEndpointCreationPayload> createGraphQlEndpoint(
            @NonNull @Argument final GraphQlEndpointCreationInput input) {

        log.info("createGraphQlEndpoint input={}", input);

        return graphQlSchemaRegistryService.validateNewSchema(input.getSchema())
                .doOnSuccess(unused -> this.validate(input))
                .flatMap(unused -> persistenceServiceFacade.graphQlEndpointService().create(input))
                .doOnSuccess(unused -> graphQlSchemaRegistryService.reloadSchemas())
                .map(e -> (GraphQlEndpointCreationPayload) GraphQlEndpointCreationResponse.builder()
                        .graphQlEndpoint(e)
                        .build())
                .onErrorResume(BadSchemaException.class, ex -> Mono.just(GraphQlEndpointCreationErrors.builder()
                        .errors(List.of(GraphQlEndpointErrorBadSchema.builder()
                                .message("Schema is incorrect")
                                .schema(ex.getSchema())
                                .build()))
                        .build()))
                .onErrorResume(EndpointEmptyNameException.class, ex -> Mono.just(GraphQlEndpointCreationErrors.builder()
                        .errors(List.of(EndpointErrorEmptyName.builder().message("Empty endpoint name").build()))
                        .build()))
                .onErrorResume(EndpointDuplicatingNameException.class, ex -> Mono.just(
                        GraphQlEndpointCreationErrors.builder()
                                .errors(List.of(EndpointErrorDuplicatingName.builder()
                                        .message("There is already an endpoint with the given name")
                                        .name(ex.getName())
                                        .build()))
                                .build()));
    }

    private void validate(@NonNull final GraphQlEndpointCreationInput input) {
        if (StringUtils.isBlank(input.getName())) {
            throw new EndpointEmptyNameException();
        }
    }
}
