package com.vetka.gateway.schema.bo;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointInfo {

    @NonNull
    private final GraphQlEndpoint graphQlEndpoint;

    @NonNull
    private final Set<String> queries, mutations, subscriptions;

    @Override
    public String toString() {
        return "GraphQlEndpointInfo{" + "graphQlEndpoint=" + graphQlEndpoint + ", queries=" + queries.size() + ", mutations=" + mutations.size() + ", subscriptions=" + subscriptions.size() + '}';
    }
}
