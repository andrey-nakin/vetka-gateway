package com.vetka.gateway.schema.bo;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GraphQlEndpointInfo {

    private final GraphQlEndpoint graphQlEndpoint;

    private final Set<String> queries, mutations, subscriptions;
}
