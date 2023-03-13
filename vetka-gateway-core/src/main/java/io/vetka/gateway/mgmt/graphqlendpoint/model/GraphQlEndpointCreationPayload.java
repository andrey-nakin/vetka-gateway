package io.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointCreationPayload implements GraphQlEndpointCreationResponse {

    @NonNull
    private final GraphQlEndpoint graphQlEndpoint;
}
