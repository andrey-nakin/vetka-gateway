package io.vetka.gateway.schema.service;

import graphql.ExecutionResult;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.vetka.gateway.endpoint.GatewayWebGraphQlHandler;
import io.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import io.vetka.gateway.graphql.GraphQlQueryBuilder;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.transport.api.ITransportService;
import java.util.Collection;
import java.util.IdentityHashMap;
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
import org.springframework.http.HttpHeaders;

@RequiredArgsConstructor
@Slf4j
public class GraphQlClientLoader implements MappedBatchLoader<DataFetchingEnvironment, DataFetcherResult<Object>> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Map<DataFetchingEnvironment, DataFetcherResult<Object>>> load(
            final Set<DataFetchingEnvironment> keys) {
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
            final var context = keys.iterator().next().getGraphQlContext();
            final WebGraphQlRequestWrapper rw = context.get(GatewayWebGraphQlHandler.REQUEST_WRAPPER);
            httpHeaders = rw.request().getHeaders();
        } else {
            httpHeaders = HttpHeaders.EMPTY;
        }

        return new QueryData(keyMap, httpHeaders, query);
    }

    private CompletionStage<Map<DataFetchingEnvironment, DataFetcherResult<Object>>> sendQuery(
            final QueryData queryData) {
        return transportService.request(queryData.httpHeaders(), queryData.query(), graphQlEndpointInfo)
                .thenApply(response -> parseQueryResponse(queryData, response));
    }

    private Map<DataFetchingEnvironment, DataFetcherResult<Object>> parseQueryResponse(final QueryData queryData,
            final ExecutionResult response) {

        final var result = new IdentityHashMap<DataFetchingEnvironment, DataFetcherResult<Object>>();

        final Map<String, Object> data = response.getData();
        if (data != null) {
            data.forEach((key, value) -> {
                final var env = queryData.keyMap().get(key);
                result.put(env, DataFetcherResult.newResult().data(value).localContext(env.getLocalContext()).build());
            });
        }

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            if (result.isEmpty()) {
                if (!queryData.keyMap().isEmpty()) {
                    final var env = any(queryData.keyMap().values());
                    result.put(env, DataFetcherResult.newResult()
                            .errors(response.getErrors())
                            .localContext(env.getLocalContext())
                            .build());
                }
            } else {
                final var firstKey = any(result.keySet());
                result.put(firstKey, result.get(firstKey).transform(b -> b.errors(response.getErrors())));
            }
        }

        // TODO handle extensions

        return result;
    }

    private static <T> T any(final Collection<T> coll) {
        return coll.iterator().next();
    }

    private record QueryData(Map<String, DataFetchingEnvironment> keyMap, HttpHeaders httpHeaders, String query) {}
}
