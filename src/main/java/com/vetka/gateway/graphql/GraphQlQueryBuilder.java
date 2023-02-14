package com.vetka.gateway.graphql;

import com.vetka.gateway.schema.service.GraphQlConstants;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.SelectedField;
import java.util.Set;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class GraphQlQueryBuilder {

    public static String build(final Set<DataFetchingEnvironment> fields) {
        final var sb = new StringBuilder();
        sb.append("{\"query\":\"");

        final var typeName = ((GraphQLObjectType) fields.iterator().next().getParentType()).getName();
        sb.append(switch (typeName) {
            case GraphQlConstants.TYPE_QUERY -> "query";
            case GraphQlConstants.TYPE_MUTATION -> "mutation";
            default -> throw new IllegalArgumentException("Unsupported type: " + typeName);
        }).append(" {\\n");
        fields.forEach(key -> {
            final var field = key.getField();

            if (StringUtils.isNotBlank(field.getAlias())) {
                sb.append(field.getAlias()).append(": ");
            }
            sb.append(field.getName());

            if (field.getArguments() != null && !field.getArguments().isEmpty()) {
                // TODO
            }

            sb.append(" {\\n");
            key.getSelectionSet().getImmediateFields().forEach(selField -> addField(sb, selField));
            sb.append("}\\n");
        });
        sb.append("}\\n");
        sb.append("\",\"variables\":{}}");

        return sb.toString();
    }

    private static void addField(final StringBuilder sb, final SelectedField field) {
        if (StringUtils.isNotBlank(field.getAlias())) {
            sb.append(field.getAlias()).append(": ");
        }
        sb.append(field.getName());

        if (field.getArguments() != null && !field.getArguments().isEmpty()) {
            // TODO
        }

        if (field.getSelectionSet() != null && field.getSelectionSet()
                .getImmediateFields() != null && !field.getSelectionSet().getImmediateFields().isEmpty()) {
            sb.append(" {\\n");
            field.getSelectionSet().getImmediateFields().forEach(subField -> addField(sb, subField));
            sb.append("}\\n");
        }

        sb.append("\\n");
    }
}
