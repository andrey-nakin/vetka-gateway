package com.vetka.gateway.transport.api.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlJsonMappingException extends GraphQlTransportException {

    public GraphQlJsonMappingException(final JsonMappingException cause,
            final GraphQlEndpointInfo graphQlEndpointInfo) {
        super("Error mapping GraphQL Response from " + graphQlEndpointInfo.getGraphQlEndpoint().getAddress(), cause);
    }
}