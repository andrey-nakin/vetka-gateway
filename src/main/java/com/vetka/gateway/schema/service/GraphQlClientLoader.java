package com.vetka.gateway.schema.service;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;

@RequiredArgsConstructor
public class GraphQlClientLoader implements MappedBatchLoaderWithContext<DataFetchingEnvironment, Object> {

    @Getter
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Map<DataFetchingEnvironment, Object>> load(Set<DataFetchingEnvironment> keys,
            BatchLoaderEnvironment environment) {

        return CompletableFuture.completedFuture(keys.stream()
                .collect(Collectors.toMap(Function.identity(), key -> List.of(Map.of("name",
                        "Russia " + ((GraphQLObjectType) key.getParentType()).getName() + "/" + key.getField()
                                .getName() + "/" + key.getField().getAlias())))));
    }
}
