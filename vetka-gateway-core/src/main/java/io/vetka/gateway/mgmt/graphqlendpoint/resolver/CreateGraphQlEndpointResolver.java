package io.vetka.gateway.mgmt.graphqlendpoint.resolver;

import graphql.schema.DataFetchingEnvironment;
import io.vetka.gateway.mgmt.common.mapper.GraphQLErrorMapper;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorDuplicatingName;
import io.vetka.gateway.mgmt.endpoint.model.EndpointErrorEmptyName;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationError;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationErrors;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationPayload;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationResponse;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointErrorBadSchema;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointDuplicatingNameException;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointEmptyNameException;
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
public class CreateGraphQlEndpointResolver {

    private final IGraphQlEndpointService graphQlEndpointService;
    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;
    private final GraphQLErrorMapper graphQLErrorMapper;

    @MutationMapping
    public Mono<GraphQlEndpointCreationPayload> createGraphQlEndpoint(@NonNull DataFetchingEnvironment environment) {
        log.info("createGraphQlEndpoint");

        return validate(environment).flatMap(graphQlEndpointService::create)
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

    private Mono<Map<String, Object>> validate(@NonNull final DataFetchingEnvironment environment) {
        final Map<String, Object> input = environment.getArgument("input");

        if (input.containsKey("name") && StringUtils.isBlank((String) input.get("name"))) {
            return Mono.error(new EndpointEmptyNameException());
        }

        return graphQlSchemaRegistryService.validateNewSchema((String) input.get("schema")).map(validatedSchema -> {
            final var result = new HashMap<>(input);
            result.put("schema", validatedSchema);
            return result;
        });
    }
}
