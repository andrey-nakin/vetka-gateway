package com.vetka.gateway.schema.bo;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import graphql.schema.GraphQLSchema;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
@ToString
public class GraphQlEndpointInfo {

    @NonNull
    private final GraphQlEndpoint graphQlEndpoint;

    @NonNull
    private final GraphQLSchema schema;

    private final Map<String, Object> typedCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getCachedValue(@NonNull final String key, @NonNull final Supplier<T> supplier) {
        return (T) typedCache.computeIfAbsent(key, unused -> supplier.get());
    }
}
