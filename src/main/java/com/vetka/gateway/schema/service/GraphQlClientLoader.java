package com.vetka.gateway.schema.service;

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
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;

@RequiredArgsConstructor
@Slf4j
public class GraphQlClientLoader implements MappedBatchLoaderWithContext<DataFetchingEnvironment, Object> {

    private final ITransportService transportService;
    private final GraphQlEndpointInfo graphQlEndpointInfo;

    @Override
    public CompletionStage<Map<DataFetchingEnvironment, Object>> load(Set<DataFetchingEnvironment> keys,
            BatchLoaderEnvironment environment) {

        final var keyMap = keys.stream()
                .collect(Collectors.toMap(
                        k -> StringUtils.defaultIfBlank(k.getField().getAlias(), k.getField().getName()),
                        Function.identity()));

        final var query = GraphQlQueryBuilder.build(keys);
        log.debug("Sending query to {}\n{}", graphQlEndpointInfo.getGraphQlEndpoint().getAddress(), query);

        return transportService.request(query, graphQlEndpointInfo).thenApply(response -> {
            final var result = new HashMap<DataFetchingEnvironment, Object>();
            final Map<String, Object> data = response.getData();
            if (data != null) {
                data.forEach((key, value) -> result.put(keyMap.get(key), value));
            }
            return result;
        });
    }
}
