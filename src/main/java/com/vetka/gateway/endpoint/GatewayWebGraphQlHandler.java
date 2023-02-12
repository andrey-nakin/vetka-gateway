package com.vetka.gateway.endpoint;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import com.vetka.gateway.transport.api.ITransportService;
import com.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ResponseError;
import org.springframework.graphql.ResponseField;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayWebGraphQlHandler {

    private final GraphQlSchemaRegistryService graphQlSchemaRegistryService;
    private final ITransportService transportService;

    public Mono<ServerResponse> handleRequest(@NonNull final ServerRequest serverRequest,
            @NonNull final WebGraphQlRequestWrapper requestWrapper) {

        final var request = requestWrapper.request();
        log.info("handleRequest {}", request.getDocument());

        final var schemaInfo = graphQlSchemaRegistryService.getInfo();
        final var build = GraphQL.newGraphQL(schemaInfo.getSchema()).build();
        final var executionInput = requestWrapper.request()
                .toExecutionInput()
                .transform(b -> b.localContext(new GatewayLocalContext(schemaInfo)));
        final var executionResult = build.execute(executionInput);

        if (false) {
            final var endpoints = schemaInfo.getEndpoints();
            if (!endpoints.isEmpty()) {
                return transportService.proxyRequest(serverRequest, requestWrapper, endpoints.get(0));
            }
        }

        final var response = new WebGraphQlResponse(new ExecutionGraphQlResponse() {
            @Override
            public ExecutionInput getExecutionInput() {
                return executionInput;
            }

            @Override
            public ExecutionResult getExecutionResult() {
                return executionResult;
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public <T> T getData() {
                return executionResult.getData();
            }

            @Override
            public List<ResponseError> getErrors() {
                return null;
            }

            @Override
            public ResponseField field(String path) {
                return null;
            }

            @Override
            public Map<Object, Object> getExtensions() {
                return null;
            }

            @Override
            public Map<String, Object> toMap() {
                return null;
            }
        });

        return toServerResponse(response);
    }

    private Mono<ServerResponse> toServerResponse(final WebGraphQlResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("complete");
        }
        final ServerResponse.BodyBuilder builder = ServerResponse.ok();
        builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
        builder.contentType(GatewayHttpHandler.MEDIA_TYPE);
        return builder.bodyValue(response.toMap());
    }
}
