package com.vetka.gateway.transport.api.exception;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlHttpErrorException extends GraphQlTransportException {

    public GraphQlHttpErrorException(final int httpStatus, final GraphQlEndpointInfo graphQlEndpointInfo) {
        super("GraphQL request to " + graphQlEndpointInfo.getGraphQlEndpoint()
                .getAddress() + " returned status " + httpStatus);
    }
}
