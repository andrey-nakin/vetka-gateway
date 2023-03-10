package io.vetka.gateway.endpoint;

import io.vetka.gateway.endpoint.properties.EndpointProperties;
import io.vetka.gateway.schema.service.GraphQlConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouter {

    private static final String DEFAULT_PATH = "/";

    @Bean
    public RouterFunction<ServerResponse> route(final EndpointProperties endpointProperties,
            final GatewayHttpHandler gatewayHttpHandler) {

        return RouterFunctions.route(
                RequestPredicates.POST(StringUtils.defaultIfBlank(endpointProperties.getPath(), DEFAULT_PATH))
                        .and(RequestPredicates.accept(GraphQlConstants.MEDIA_TYPE)), gatewayHttpHandler::handleRequest);
    }
}
