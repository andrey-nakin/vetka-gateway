package com.vetka.gateway.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouter {

    @Bean
    public RouterFunction<ServerResponse> route(final GatewayEndpoint gatewayEndpoint) {
        return RouterFunctions.route(
                RequestPredicates.POST("/").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                gatewayEndpoint::handleRequest);
    }
}
