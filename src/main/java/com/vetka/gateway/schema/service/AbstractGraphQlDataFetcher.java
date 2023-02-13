package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletionStage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

@RequiredArgsConstructor
public abstract class AbstractGraphQlDataFetcher implements DataFetcher<CompletionStage<Object>> {

    @Getter
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Object> get(DataFetchingEnvironment environment) {
        final GatewayLocalContext context = environment.getLocalContext();

        final var key = graphQlEndpointInfo.getGraphQlEndpoint().getId();
        DataLoader<DataFetchingEnvironment, Object> dataLoader = context.getDataLoaderRegistry()
                .computeIfAbsent(key,
                        unused -> DataLoaderFactory.newMappedDataLoader(new GraphQlClientLoader(graphQlEndpointInfo)));
        return dataLoader.load(environment);
    }
}
