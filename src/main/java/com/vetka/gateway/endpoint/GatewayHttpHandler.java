package com.vetka.gateway.endpoint;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayHttpHandler {

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

            return gatewayWebGraphQlHandler.handleRequest(new WebGraphQlRequestWrapper(graphQlRequest, body));
        });
    }
}
