package io.vetka.gateway.transport.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;

public class GraphQlJsonProcessingException extends GraphQlTransportException {

    public GraphQlJsonProcessingException(JsonProcessingException cause,
            final GraphQlEndpointInfo graphQlEndpointInfo) {
        super("Error processing GraphQL Response from " + graphQlEndpointInfo.getGraphQlEndpoint().getAddress(), cause);
    }
}
