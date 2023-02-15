package com.vetka.gateway.graphql;

import com.vetka.gateway.schema.service.GraphQlConstants;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.SelectedField;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

            addArguments(sb, field.getArguments());

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

        addArguments(sb, field.getArguments());

        if (field.getSelectionSet() != null && field.getSelectionSet()
                .getImmediateFields() != null && !field.getSelectionSet().getImmediateFields().isEmpty()) {
            sb.append(" {\\n");
            field.getSelectionSet().getImmediateFields().forEach(subField -> addField(sb, subField));
            sb.append("}\\n");
        }

        sb.append("\\n");
    }

    private static void addArguments(final StringBuilder sb, final List<Argument> arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            sb.append("(\\n");
            for (final var arg : arguments) {
                sb.append(arg.getName()).append(": ");
                addValue(sb, arg.getValue());
                sb.append("\\n");
            }
            sb.append(")\\n");
        }
    }

    private static void addArguments(final StringBuilder sb, final Map<String, Object> arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            sb.append("(\\n");
            arguments.forEach((name, value) -> {
                sb.append(name).append(": ");
                addValue(sb, value);
                sb.append("\\n");
            });
            sb.append(")\\n");
        }
    }

    private static void addValue(final StringBuilder sb, final Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String strVal) {
            sb.append('"').append(prepareString(strVal)).append('"');
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Collection<?> coll) {
            sb.append("[\\n");
            for (final var val : coll) {
                addValue(sb, val);
            }
            sb.append("]\\n");
        } else if (value instanceof Map<?, ?> map) {
            sb.append("{\\n");
            map.forEach((name, val) -> {
                sb.append(name).append(": ");
                addValue(sb, val);
            });
            sb.append("}\\n");
        } else if (value instanceof StringValue stringValue) {
            sb.append('"').append(prepareString(stringValue.getValue())).append('"');
        } else if (value instanceof IntValue intVal) {
            sb.append(intVal.getValue());
        } else if (value instanceof FloatValue floatVal) {
            sb.append(floatVal.getValue());
        } else if (value instanceof BooleanValue boolVal) {
            sb.append(boolVal.isValue());
        } else if (value instanceof ArrayValue arrayVal) {
            sb.append("[\\n");
            for (final var val : arrayVal.getValues()) {
                addValue(sb, val);
            }
            sb.append("]\\n");
        } else if (value instanceof EnumValue enumVal) {
            sb.append(enumVal.getName());
        } else {
            throw new IllegalArgumentException("Unsupported value " + value + " of type " + value.getClass().getName());
        }
    }

    private static String prepareString(final String str) {
        return str.replaceAll("\"", "\\\"").replaceAll("\n", "\\n");
    }
}
