package com.vetka.gateway.schema.service;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlQueryDataFetcher extends AbstractGraphQlDataFetcher {

    public GraphQlQueryDataFetcher(String fieldName, GraphQlEndpointInfo graphQlEndpointInfo) {
        super(fieldName, graphQlEndpointInfo);
    }
}
