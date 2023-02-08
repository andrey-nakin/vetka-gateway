package com.vetka.gateway.endpoint;

import com.vetka.gateway.endpoint.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouter {

    @Bean
    public RouterFunction<ServerResponse> route(final GatewayProperties gatewayProperties,
            final GatewayHttpHandler gatewayHttpHandler) {

        return RouterFunctions.route(RequestPredicates.POST(gatewayProperties.getPath())
                .and(RequestPredicates.accept(GatewayHttpHandler.MEDIA_TYPE)), gatewayHttpHandler::handleRequest);
    }
}