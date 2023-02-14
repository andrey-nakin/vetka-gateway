package com.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointUpdateResponse implements GraphQlEndpointUpdatePayload {

    @NonNull
    private final GraphQlEndpoint graphQlEndpoint;
}
