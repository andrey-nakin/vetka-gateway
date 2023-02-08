package com.vetka.gateway.endpoint;

import java.util.List;
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

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_PARAMETERIZED_TYPE_REF =
            new ParameterizedTypeReference<>() {};
    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = List.of(MediaType.APPLICATION_JSON);

    private final GatewayWebGraphQlHandler gatewayWebGraphQlHandler;

    public Mono<ServerResponse> handleRequest(final ServerRequest serverRequest) {
        return serverRequest.bodyToMono(MAP_PARAMETERIZED_TYPE_REF).flatMap(body -> {
            final WebGraphQlRequest graphQlRequest =
                    new WebGraphQlRequest(serverRequest.uri(), serverRequest.headers().asHttpHeaders(), body,
                            serverRequest.exchange().getRequest().getId(),
                            serverRequest.exchange().getLocaleContext().getLocale());
            if (log.isDebugEnabled()) {
                log.debug("Executing: {}", graphQlRequest);
            }

            return gatewayWebGraphQlHandler.handleRequest(graphQlRequest);
        }).flatMap(response -> {
            if (log.isDebugEnabled()) {
                log.debug("Execution complete");
            }
            final ServerResponse.BodyBuilder builder = ServerResponse.ok();
            builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
            builder.contentType(selectResponseMediaType(serverRequest));
            return builder.bodyValue(response.toMap());
        });
    }

    private static MediaType selectResponseMediaType(ServerRequest serverRequest) {
        for (MediaType accepted : serverRequest.headers().accept()) {
            if (SUPPORTED_MEDIA_TYPES.contains(accepted)) {
                return accepted;
            }
        }
        return MediaType.APPLICATION_JSON;
    }
}
