package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractGraphQlDataFetcher implements DataFetcher {

    private final String fieldName;
    @Getter
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final GatewayLocalContext context = environment.getLocalContext();
        return List.of(Map.of("name", "Russia"));
    }
}
