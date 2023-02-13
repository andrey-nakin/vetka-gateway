package com.vetka.gateway.schema.service;

import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import com.vetka.gateway.transport.api.ITransportService;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.SelectedField;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.MappedBatchLoaderWithContext;

@RequiredArgsConstructor
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

        final var sb = new StringBuilder();
        sb.append("{\"query\":\"");

        final var typeName = ((GraphQLObjectType) keys.iterator().next().getParentType()).getName();
        sb.append(switch (typeName) {
            case "Query" -> "query";
            case "Mutation" -> "mutation";
            default -> throw new IllegalArgumentException("Unsupported type: " + typeName);
        }).append(" {\\n");
        keys.forEach(key -> {
            final var field = key.getField();

            if (StringUtils.isNotBlank(field.getAlias())) {
                sb.append(field.getAlias()).append(": ");
            }
            sb.append(field.getName());

            if (field.getArguments() != null && !field.getArguments().isEmpty()) {
                // TODO
            }

            sb.append(" {\\n");
            key.getSelectionSet().getFields().forEach(selField -> addField(sb, selField));
            sb.append("}\\n");
        });
        sb.append("}\\n");
        sb.append("\",\"variables\":{}}");

        final var query = sb.toString();

        return transportService.request(query, graphQlEndpointInfo).thenApply(response -> {
            final var result = new HashMap<DataFetchingEnvironment, Object>();
            final Map<String, Object> data = response.getData();
            if (data != null) {
                data.forEach((key, value) -> result.put(keyMap.get(key), value));
            }
            return result;
        });

        //        return CompletableFuture.completedFuture(keys.stream()
        //                .collect(Collectors.toMap(Function.identity(), key -> List.of(Map.of("name",
        //                        "Russia " + ((GraphQLObjectType) key.getParentType()).getName() + "/" + key.getField()
        //                                .getName() + "/" + key.getField().getAlias())))));
    }

    private static void addField(final StringBuilder sb, final SelectedField field) {
        if (StringUtils.isNotBlank(field.getAlias())) {
            sb.append(field.getAlias()).append(": ");
        }
        sb.append(field.getName());

        if (field.getArguments() != null && !field.getArguments().isEmpty()) {
            // TODO
        }

        if (field.getSelectionSet() != null && field.getSelectionSet().getFields() != null && !field.getSelectionSet()
                .getFields()
                .isEmpty()) {
            sb.append(" {\\n");
            field.getSelectionSet().getFields().forEach(subField -> addField(sb, subField));
            sb.append("}\\n");
        }

        sb.append("\\n");
    }
}
