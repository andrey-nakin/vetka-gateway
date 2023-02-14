package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.common.mapper.GraphQLErrorMapper;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorEmptyName;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationError;
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
    private final GraphQLErrorMapper graphQLErrorMapper;

    @MutationMapping
    public Mono<GraphQlEndpointCreationPayload> createGraphQlEndpoint(
            @NonNull @Argument final GraphQlEndpointCreationInput input) {

        log.info("createGraphQlEndpoint input={}", input);

        return validate(input).flatMap(
                        validatedInput -> persistenceServiceFacade.graphQlEndpointService().create(validatedInput))
                .doOnSuccess(unused -> graphQlSchemaRegistryService.reloadSchemas())
                .map(e -> (GraphQlEndpointCreationPayload) GraphQlEndpointCreationResponse.builder()
                        .graphQlEndpoint(e)
                        .build())
                .onErrorResume(BadSchemaException.class, ex -> error(GraphQlEndpointErrorBadSchema.builder()
                        .message("Schema is incorrect")
                        .schema(ex.getSchema())
                        .errors(graphQLErrorMapper.toModels(ex.getErrors()))
                        .build()))
                .onErrorResume(EndpointEmptyNameException.class,
                        ex -> error(EndpointErrorEmptyName.builder().message("Empty endpoint name").build()))
                .onErrorResume(EndpointDuplicatingNameException.class, ex -> error(
                        EndpointErrorDuplicatingName.builder()
                                .message("There is already an endpoint with the given name")
                                .name(ex.getName())
                                .build()));
    }

    private static Mono<GraphQlEndpointCreationPayload> error(final GraphQlEndpointCreationError error) {
        return Mono.just(GraphQlEndpointCreationErrors.builder().errors(List.of(error)).build());
    }

    private Mono<GraphQlEndpointCreationInput> validate(@NonNull final GraphQlEndpointCreationInput input) {
        if (StringUtils.isBlank(input.getName())) {
            return Mono.error(new EndpointEmptyNameException());
        }

        return graphQlSchemaRegistryService.validateNewSchema(input.getSchema())
                .map(validatedSchema -> input.toBuilder().schema(validatedSchema).build());
    }
}
