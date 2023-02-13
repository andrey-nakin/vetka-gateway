package com.vetka.gateway.schema.service;

import com.vetka.gateway.endpoint.GatewayLocalContext;
import com.vetka.gateway.graphql.GraphQlQueryBuilder;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.transport.api.ITransportService;
import graphql.schema.DataFetchingEnvironment;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
public class GraphQlClientLoader implements MappedBatchLoader<DataFetchingEnvironment, Object> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Map<DataFetchingEnvironment, Object>> load(final Set<DataFetchingEnvironment> keys) {
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

        return transportService.request(httpHeaders, query, graphQlEndpointInfo).thenApply(response -> {
            final var result = new HashMap<DataFetchingEnvironment, Object>();
            final Map<String, Object> data = response.getData();
            if (data != null) {
                data.forEach((key, value) -> result.put(keyMap.get(key), value));
            }
            // TODO handle errors and extensions
            return result;
        });
    }
}
