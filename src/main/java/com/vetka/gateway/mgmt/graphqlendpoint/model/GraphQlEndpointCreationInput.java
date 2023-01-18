package com.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class GraphQlEndpointCreationInput {

    private final String name;
    @NonNull
    private final String address;
    @NonNull
    private final String schema;

    @Override
    public String toString() {
        return "GraphQlEndpointCreationInput{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", schema='" + StringUtils.truncate(
                schema, 50) + '\'' + '}';
    }
}
