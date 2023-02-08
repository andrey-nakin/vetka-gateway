package com.vetka.gateway.endpoint;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayHttpHandler {

    public static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private static final ParameterizedTypeReference<Map<String, Object>> TYPE_REF =
            new ParameterizedTypeReference<>() {};

    private final GatewayWebGraphQlHandler gatewayWebGraphQlHandler;

    public Mono<ServerResponse> handleRequest(final ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TYPE_REF).flatMap(body -> {
            final WebGraphQlRequest graphQlRequest =
                    new WebGraphQlRequest(serverRequest.uri(), serverRequest.headers().asHttpHeaders(), body,
                            serverRequest.exchange().getRequest().getId(),
                            serverRequest.exchange().getLocaleContext().getLocale());
            if (log.isDebugEnabled()) {
                log.debug("handle {}", graphQlRequest);
            }

            return gatewayWebGraphQlHandler.handleRequest(graphQlRequest);
        }).flatMap(response -> {
            if (log.isDebugEnabled()) {
                log.debug("complete");
            }
            final ServerResponse.BodyBuilder builder = ServerResponse.ok();
            builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
            builder.contentType(MEDIA_TYPE);
            return builder.bodyValue(response.toMap());
        });
    }
}
