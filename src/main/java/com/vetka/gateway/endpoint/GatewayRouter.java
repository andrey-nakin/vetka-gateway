package com.vetka.gateway.endpoint;

import com.vetka.gateway.endpoint.properties.EndpointProperties;
import com.vetka.gateway.schema.service.GraphQlConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouter {

    @Bean
    public RouterFunction<ServerResponse> route(final EndpointProperties endpointProperties,
            final GatewayHttpHandler gatewayHttpHandler) {

        return RouterFunctions.route(RequestPredicates.POST(endpointProperties.getPath())
                .and(RequestPredicates.accept(GraphQlConstants.MEDIA_TYPE)), gatewayHttpHandler::handleRequest);
    }
}
