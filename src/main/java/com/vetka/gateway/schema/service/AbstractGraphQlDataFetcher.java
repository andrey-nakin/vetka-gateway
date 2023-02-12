package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
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
import org.dataloader.DataLoaderFactory;
import org.dataloader.MappedBatchLoaderWithContext;

@RequiredArgsConstructor
public abstract class AbstractGraphQlDataFetcher implements DataFetcher<CompletionStage<Object>> {

    private final String fieldName;
    @Getter
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Object> get(DataFetchingEnvironment environment) {
        final GatewayLocalContext context = environment.getLocalContext();

        final var loader = new MappedBatchLoaderWithContext<DataFetchingEnvironment, Object>() {
            @Override
            public CompletionStage<Map<DataFetchingEnvironment, Object>> load(Set<DataFetchingEnvironment> keys,
                    BatchLoaderEnvironment environment) {

                final var result = List.of(Map.of("name", "Russia " + context.getDataLoaderRegistry()));
                return CompletableFuture.completedFuture(
                        keys.stream().collect(Collectors.toMap(Function.identity(), unused -> result)));
            }
        };
        final var dataLoader = DataLoaderFactory.newMappedDataLoader(loader);
        context.getDataLoaderRegistry().register(getClass().getName() + "." + fieldName, dataLoader);
        return dataLoader.load(environment);
    }
}
