package com.vetka.gateway.schema.service;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlSubscriptionDataFetcher extends AbstractGraphQlDataFetcher {

    public GraphQlSubscriptionDataFetcher(String fieldName, GraphQlEndpointInfo graphQlEndpointInfo) {
        super(fieldName, graphQlEndpointInfo);
    }
}
