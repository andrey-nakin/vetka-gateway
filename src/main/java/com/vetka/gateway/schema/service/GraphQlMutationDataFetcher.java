package com.vetka.gateway.schema.service;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlMutationDataFetcher extends AbstractGraphQlDataFetcher {

    public GraphQlMutationDataFetcher(String fieldName, GraphQlEndpointInfo graphQlEndpointInfo) {
        super(fieldName, graphQlEndpointInfo);
    }
}
