package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.graphql.GraphQlQueryBuilder;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.transport.api.ITransportService;
import graphql.schema.DataFetchingEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dataloader.MappedBatchLoader;
import org.springframework.graphql.GraphQlResponse;
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
@Slf4j
public class GraphQlClientLoader implements MappedBatchLoader<DataFetchingEnvironment, Object> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Map<DataFetchingEnvironment, Object>> load(final Set<DataFetchingEnvironment> keys) {
        return CompletableFuture.supplyAsync(() -> prepareQuery(keys)).thenCompose(this::sendQuery);
    }

    private QueryData prepareQuery(final Set<DataFetchingEnvironment> keys) {
        final var keyMap = keys.stream()
                .collect(Collectors.toMap(
                        k -> StringUtils.defaultIfBlank(k.getField().getAlias(), k.getField().getName()),
                        Function.identity()));

        final var query = GraphQlQueryBuilder.build(keys);
        log.debug("Sending GraphQL request to {}: {}", graphQlEndpointInfo.getGraphQlEndpoint().getAddress(), query);

        final HttpHeaders httpHeaders;
        if (!keys.isEmpty()) {
            final GatewayLocalContext context = keys.iterator().next().getLocalContext();
            httpHeaders = context.getRequestWrapper().request().getHeaders();
        } else {
            httpHeaders = HttpHeaders.EMPTY;
        }

        return new QueryData(keyMap, httpHeaders, query);
    }

    private CompletionStage<Map<DataFetchingEnvironment, Object>> sendQuery(final QueryData queryData) {
        return transportService.request(queryData.httpHeaders(), queryData.query(), graphQlEndpointInfo)
                .thenApply(response -> parseQueryResponse(queryData, response));
    }

    private Map<DataFetchingEnvironment, Object> parseQueryResponse(final QueryData queryData,
            final GraphQlResponse response) {
        final var result = new HashMap<DataFetchingEnvironment, Object>();
        final Map<String, Object> data = response.getData();
        if (data != null) {
            data.forEach((key, value) -> result.put(queryData.keyMap().get(key), value));
        }
        // TODO handle errors and extensions
        return result;
    }

    private record QueryData(Map<String, DataFetchingEnvironment> keyMap, HttpHeaders httpHeaders, String query) {}
}
