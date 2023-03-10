package io.vetka.gateway.endpoint;

import graphql.GraphQL;
import io.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import io.vetka.gateway.schema.service.GraphQlConstants;
import io.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoaderRegistry;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.support.DefaultExecutionGraphQlResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayWebGraphQlHandler {

    public static final String REQUEST_WRAPPER = "__rw";

    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    public Mono<ServerResponse> handleRequest(@NonNull final WebGraphQlRequestWrapper requestWrapper) {
        final var request = requestWrapper.request();
        log.debug("handleRequest {}", request.getDocument());

        final var schemaInfo = graphQlSchemaRegistryService.getInfo();
        final var build = GraphQL.newGraphQL(schemaInfo.getSchema()).build();
        requestWrapper.request()
                .configureExecutionInput((ei, b) -> b.dataLoaderRegistry(new DataLoaderRegistry()).build());
        final var executionInput = requestWrapper.request().toExecutionInput();
        executionInput.getGraphQLContext().put(REQUEST_WRAPPER, requestWrapper);
        return Mono.fromCompletionStage(build.executeAsync(executionInput))
                .map(executionResult -> new DefaultExecutionGraphQlResponse(executionInput, executionResult))
                .map(WebGraphQlResponse::new)
                .flatMap(this::toServerResponse);
    }

    private Mono<ServerResponse> toServerResponse(final WebGraphQlResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("complete");
        }
        final ServerResponse.BodyBuilder builder = ServerResponse.ok();
        builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
        builder.contentType(GraphQlConstants.MEDIA_TYPE);
        return builder.bodyValue(response.toMap());
    }
}
