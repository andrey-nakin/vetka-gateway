package com.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointCreationInput {

    @NonNull
    private final String name;
    @NonNull
    private final String address;
    @NonNull
    private final String schema;
}
