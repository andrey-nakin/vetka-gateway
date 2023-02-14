package com.vetka.gateway.mgmt.graphqlendpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder(toBuilder = true)
public class GraphQlEndpointUpdateInput {

    @NonNull
    private final String id;
    private final String name;
    private final String address;
    private final String schema;
    private final String httpVersion;
    private final Integer connectTimeout;
    private final Integer readTimeout;

    @Override
    public String toString() {
        return "GraphQlEndpointUpdateInput{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", address='" + address + '\'' + ", schema='" + StringUtils.truncate(
                schema,
                50) + '\'' + ", httpVersion='" + httpVersion + '\'' + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout + '}';
    }
}
