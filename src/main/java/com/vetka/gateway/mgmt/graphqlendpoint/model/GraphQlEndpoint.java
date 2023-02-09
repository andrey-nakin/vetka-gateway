package com.vetka.gateway.mgmt.graphqlendpoint.model;

import com.vetka.gateway.mgmt.endpoint.model.IEndpoint;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class GraphQlEndpoint implements IEndpoint {

    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String address;
    @NonNull
    private final String schema;
    private final String httpVersion;
    private final Integer connectTimeout;
    private final Integer readTimeout;

    @Override
    public String toString() {
        return "GraphQlEndpoint{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", address='" + address + '\'' + ", schema='" + StringUtils.truncate(
                schema, 50) + '\'' + '}';
    }
}
