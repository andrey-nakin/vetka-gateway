package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.transport.api.ITransportService;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

@RequiredArgsConstructor
public class GraphQlDataFetcher implements DataFetcher<CompletionStage<Object>> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Object> get(final DataFetchingEnvironment environment) {
        final GatewayLocalContext context = environment.getLocalContext();

        final var key = graphQlEndpointInfo.getGraphQlEndpoint().getId();
        final DataLoader<DataFetchingEnvironment, Object> dataLoader = context.getDataLoaderRegistry()
                .computeIfAbsent(key, unused -> DataLoaderFactory.newMappedDataLoader(
                        new GraphQlClientLoader(transportService, graphQlEndpointInfo)));
        return dataLoader.load(environment);
    }
}
