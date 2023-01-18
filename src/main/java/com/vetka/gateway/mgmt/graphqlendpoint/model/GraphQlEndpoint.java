package com.vetka.gateway.mgmt.graphqlendpoint.model;

import com.vetka.gateway.mgmt.endpoint.model.IEndpoint;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpoint implements IEndpoint {

    @NonNull
    private final String id;
    private final String name;
    @NonNull
    private final String address;
    @NonNull
    private final String schema;
}
