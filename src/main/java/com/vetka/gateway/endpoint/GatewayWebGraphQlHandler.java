package com.vetka.gateway.endpoint;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ResponseError;
import org.springframework.graphql.ResponseField;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GatewayWebGraphQlHandler {

    public Mono<ServerResponse> handleRequest(@NonNull final WebGraphQlRequest request) {
        log.info("handleRequest {}", request.getDocument());

        final var executionInput = new ExecutionInput.Builder().executionId(request.getExecutionId())
                .query(request.getDocument())
                .operationName(request.getOperationName())
                .variables(request.getVariables())
                .extensions(request.getExtensions())
                .locale(request.getLocale())
                .build();

        final var response = new WebGraphQlResponse(new ExecutionGraphQlResponse() {
            @Override
            public ExecutionInput getExecutionInput() {
                return executionInput;
            }

            @Override
            public ExecutionResult getExecutionResult() {
                return new ExecutionResult() {
                    @Override
                    public List<GraphQLError> getErrors() {
                        return null;
                    }

                    @Override
                    public <T> T getData() {
                        return (T) "test";
                    }

                    @Override
                    public boolean isDataPresent() {
                        return false;
                    }

                    @Override
                    public Map<Object, Object> getExtensions() {
                        return null;
                    }

                    @Override
                    public Map<String, Object> toSpecification() {
                        return Map.of();
                    }
                };
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public <T> T getData() {
                return null;
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
