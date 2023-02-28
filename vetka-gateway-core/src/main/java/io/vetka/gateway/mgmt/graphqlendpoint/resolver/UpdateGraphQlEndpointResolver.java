package io.vetka.gateway.mgmt.graphqlendpoint.resolver;

import graphql.schema.DataFetchingEnvironment;
import io.vetka.gateway.mgmt.common.mapper.GraphQLErrorMapper;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorEmptyName;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorUnknownId;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointErrorBadSchema;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateError;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateErrors;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdatePayload;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateResponse;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointDuplicatingNameException;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointEmptyNameException;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import io.vetka.gateway.schema.exception.BadSchemaException;
import io.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UpdateGraphQlEndpointResolver {

    private final IGraphQlEndpointService graphQlEndpointService;
    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;
    private final GraphQLErrorMapper graphQLErrorMapper;

    @MutationMapping
    public Mono<GraphQlEndpointUpdatePayload> updateGraphQlEndpoint(
            @NonNull final DataFetchingEnvironment environment) {

        log.info("updateGraphQlEndpoint");

        return validate(environment).flatMap(graphQlEndpointService::update)
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

    private Mono<Map<String, Object>> validate(@NonNull final DataFetchingEnvironment environment) {
        final Map<String, Object> input = environment.getArgument("input");

        if (input.containsKey("name") && StringUtils.isBlank((String) input.get("name"))) {
            return Mono.error(new EndpointEmptyNameException());
        }

        if (input.containsKey("schema")) {
            return graphQlSchemaRegistryService.validateExistingSchema((String) input.get("id"),
                    (String) input.get("schema")).map(validatedSchema -> {
                final var result = new HashMap<>(input);
                result.put("schema", validatedSchema);
                return result;
            });
        }

        return Mono.just(input);
    }
}
