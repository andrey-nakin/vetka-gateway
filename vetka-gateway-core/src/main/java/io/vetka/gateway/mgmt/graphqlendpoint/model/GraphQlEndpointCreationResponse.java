package io.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointCreationResponse implements GraphQlEndpointCreationPayload {

    @NonNull
    private final GraphQlEndpoint graphQlEndpoint;
}
