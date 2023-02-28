package io.vetka.gateway.transport.api.exception;

import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlHttpErrorException extends GraphQlTransportException {

    public GraphQlHttpErrorException(final int httpStatus, final GraphQlEndpointInfo graphQlEndpointInfo) {
        super("GraphQL request to " + graphQlEndpointInfo.getGraphQlEndpoint()
                .getAddress() + " returned status " + httpStatus);
    }
}
