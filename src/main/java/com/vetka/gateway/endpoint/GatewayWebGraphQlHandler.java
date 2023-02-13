package com.vetka.gateway.endpoint;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import com.vetka.gateway.schema.service.GraphQlConstants;
import com.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import graphql.GraphQL;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.support.DefaultExecutionGraphQlResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayWebGraphQlHandler {

    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    public Mono<ServerResponse> handleRequest(@NonNull final WebGraphQlRequestWrapper requestWrapper) {
        final var request = requestWrapper.request();
        log.debug("handleRequest {}", request.getDocument());

        final var schemaInfo = graphQlSchemaRegistryService.getInfo();
        final var build = GraphQL.newGraphQL(schemaInfo.getSchema()).build();
        final var localContext = new GatewayLocalContext(schemaInfo);
        requestWrapper.request()
                .configureExecutionInput((ei, b) -> b.localContext(localContext)
                        .dataLoaderRegistry(localContext.getDataLoaderRegistry())
                        .build());
        final var executionInput = requestWrapper.request().toExecutionInput();
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
