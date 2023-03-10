package io.vetka.gateway.endpoint;

import io.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import io.vetka.gateway.schema.bo.GraphQlSchemaInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GatewayLocalContext {

    private final GraphQlSchemaInfo info;
    private final WebGraphQlRequestWrapper requestWrapper;
}
