package com.vetka.gateway.endpoint;

import com.vetka.gateway.schema.bo.GraphQlSchemaInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GatewayLocalContext {

    private final GraphQlSchemaInfo info;
}
