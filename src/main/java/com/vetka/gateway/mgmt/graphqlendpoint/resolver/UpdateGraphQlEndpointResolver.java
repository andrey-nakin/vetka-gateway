package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.common.mapper.GraphQLErrorMapper;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorEmptyName;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorUnknownId;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointErrorBadSchema;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateError;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateErrors;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdatePayload;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateResponse;
import com.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointDuplicatingNameException;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointEmptyNameException;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import com.vetka.gateway.schema.exception.BadSchemaException;
import com.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import graphql.schema.DataFetchingEnvironment;
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
public class UpdateGraphQlEndpointResolver {

    private final IPersistenceServiceFacade persistenceServiceFacade;
    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;
    private final GraphQLErrorMapper graphQLErrorMapper;

    @MutationMapping
    public Mono<GraphQlEndpointUpdatePayload> updateGraphQlEndpoint(
            @NonNull @Argument final GraphQlEndpointUpdateInput input,
            @NonNull final DataFetchingEnvironment environment) {

        log.info("updateGraphQlEndpoint input={}", input);

        return validate(input, environment).flatMap(
                        inp -> persistenceServiceFacade.graphQlEndpointService().update(inp))
                .doOnSuccess(unused -> graphQlSchemaRegistryService.reloadSchemas())
                .map(e -> (GraphQlEndpointUpdatePayload) GraphQlEndpointUpdateResponse.builder()
                        .graphQlEndpoint(e)
                        .build())
                .onErrorResume(BadSchemaException.class, ex -> error(GraphQlEndpointErrorBadSchema.builder()
                        .schema(ex.getSchema())
                        .message(ex.getMessage())
                        .errors(graphQLErrorMapper.toModels(ex.getErrors()))
                        .build()))
                .onErrorResume(EndpointNotFoundException.class, ex -> error(
                        EndpointErrorUnknownId.builder().id(ex.getId()).message("Unknown endpoint ID").build()))
                .onErrorResume(EndpointEmptyNameException.class,
                        ex -> error(EndpointErrorEmptyName.builder().message("Empty endpoint name").build()))
                .onErrorResume(EndpointDuplicatingNameException.class, ex -> error(
                        EndpointErrorDuplicatingName.builder()
                                .name(ex.getName())
                                .message("There is already an endpoint with the given name")
                                .build()));
    }

    private static Mono<GraphQlEndpointUpdatePayload> error(final GraphQlEndpointUpdateError error) {
        return Mono.just(GraphQlEndpointUpdateErrors.builder().errors(List.of(error)).build());
    }

    private Mono<GraphQlEndpointUpdateInput> validate(@NonNull final GraphQlEndpointUpdateInput input,
            @NonNull final DataFetchingEnvironment environment) {

        if (StringUtils.isBlank(input.getName())) {
            return Mono.error(new EndpointEmptyNameException());
        }

        return graphQlSchemaRegistryService.validateExistingSchema(input.getId(), input.getSchema())
                .map(validatedSchema -> input.toBuilder().schema(validatedSchema).build());
    }
}
