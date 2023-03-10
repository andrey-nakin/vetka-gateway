package io.vetka.gateway.schema.service;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.transport.api.ITransportService;
import java.util.concurrent.CompletionStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;

@RequiredArgsConstructor
@Slf4j
public class GraphQlDataFetcher implements DataFetcher<CompletionStage<DataFetcherResult<Object>>> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<DataFetcherResult<Object>> get(final DataFetchingEnvironment environment) {
        final DataLoader<DataFetchingEnvironment, DataFetcherResult<Object>> dataLoader =
                environment.getDataLoaderRegistry()
                        .computeIfAbsent(graphQlEndpointInfo.getGraphQlEndpoint().getId(), key -> {
                            log.debug("Creating data loader for key {}, address {}", key,
                                    graphQlEndpointInfo.getGraphQlEndpoint().getAddress());
                            return DataLoaderFactory.newMappedDataLoader(
                                    new GraphQlClientLoader(transportService, graphQlEndpointInfo));
                        });
        return dataLoader.load(environment);
    }
}
