package io.vetka.gateway.graphql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.VariableReference;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.SelectedField;
import io.vetka.gateway.schema.service.GraphQlConstants;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GraphQlQueryBuilder {

    private final ObjectMapper objectMapper;

    @RequiredArgsConstructor
    private class RequestContext {

        private final Set<DataFetchingEnvironment> fields;
        private final StringBuilder sb = new StringBuilder();
        private final Map<String, Object> variables = new LinkedHashMap<>();

        public StringBuilder sb() {
            return sb;
        }

        public void addVariable(final String variable) {
            variables.put(variable, fields.iterator().next().getVariables().get(variable));
        }

        public String toString() {
            try {
                final var map = new LinkedHashMap<String, Object>(4);
                map.put("query", sb.toString());
                map.put("variables", variables);
                return objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex); //  TODO
            }
        }
    }

    public String build(final Set<DataFetchingEnvironment> fields) {
        final var ctx = new RequestContext(fields);

        final var typeName = ((GraphQLObjectType) fields.iterator().next().getParentType()).getName();
        ctx.sb().append(switch (typeName) {
            case GraphQlConstants.TYPE_QUERY -> "query";
            case GraphQlConstants.TYPE_MUTATION -> "mutation";
            default -> throw new IllegalArgumentException("Unsupported type: " + typeName);
        }).append(" {\n");
        fields.forEach(key -> {
            final var field = key.getField();

            if (StringUtils.isNotBlank(field.getAlias())) {
                ctx.sb().append(field.getAlias()).append(": ");
            }
            ctx.sb().append(field.getName());

            addArguments(ctx, field.getArguments());

            ctx.sb().append(" {\n");
            ctx.sb().append("__typename\n"); //  TODO only add if __typename is not explicitly requested
            key.getSelectionSet().getImmediateFields().forEach(selField -> addField(ctx, selField));
            ctx.sb().append("}\n");
        });
        ctx.sb().append("}\n");

        return ctx.toString();
    }

    private static void addField(final RequestContext ctx, final SelectedField field) {
        for (var otn : field.getObjectTypeNames()) {
            ctx.sb().append("...on ").append(otn).append(" {\n");

            if (StringUtils.isNotBlank(field.getAlias())) {
                ctx.sb().append(field.getAlias()).append(": ");
            }
            ctx.sb().append(field.getName());

            addArguments(ctx, field.getArguments());

            if (field.getSelectionSet() != null && field.getSelectionSet()
                    .getImmediateFields() != null && !field.getSelectionSet().getImmediateFields().isEmpty()) {
                ctx.sb().append(" {\n");
                field.getSelectionSet().getImmediateFields().forEach(subField -> addField(ctx, subField));
                ctx.sb().append("}\n");
            }

            ctx.sb().append("\n}\n");
        }
    }

    private static void addArguments(final RequestContext ctx, final List<Argument> arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            ctx.sb().append("(\n");
            for (final var arg : arguments) {
                ctx.sb().append(arg.getName()).append(": ");
                addValue(ctx, arg.getValue());
                ctx.sb().append("\n");
            }
            ctx.sb().append(")\n");
        }
    }

    private static void addArguments(final RequestContext ctx, final Map<String, Object> arguments) {
        if (arguments != null && !arguments.isEmpty()) {
            ctx.sb().append("(\n");
            arguments.forEach((name, value) -> {
                ctx.sb().append(name).append(": ");
                addValue(ctx, value);
                ctx.sb().append("\n");
            });
            ctx.sb().append(")\n");
        }
    }

    private static void addValue(final RequestContext ctx, final Object value) {
        if (value == null) {
            ctx.sb().append("null");
        } else if (value instanceof String strVal) {
            ctx.sb().append("\\\"").append(prepareString(strVal)).append("\\\"");
        } else if (value instanceof Number || value instanceof Boolean) {
            ctx.sb().append(value);
        } else if (value instanceof Collection<?> coll) {
            ctx.sb().append("[\n");
            for (final var val : coll) {
                addValue(ctx, val);
            }
            ctx.sb().append("]\n");
        } else if (value instanceof Map<?, ?> map) {
            ctx.sb().append("{\n");
            map.forEach((name, val) -> {
                ctx.sb().append(name).append(": ");
                addValue(ctx, val);
            });
            ctx.sb().append("}\n");
        } else if (value instanceof StringValue stringValue) {
            ctx.sb().append("\"").append(prepareString(stringValue.getValue())).append("\"");
        } else if (value instanceof IntValue intVal) {
            ctx.sb().append(intVal.getValue());
        } else if (value instanceof FloatValue floatVal) {
            ctx.sb().append(floatVal.getValue());
        } else if (value instanceof BooleanValue boolVal) {
            ctx.sb().append(boolVal.isValue());
        } else if (value instanceof ArrayValue arrayVal) {
            ctx.sb().append("[\n");
            for (final var val : arrayVal.getValues()) {
                addValue(ctx, val);
            }
            ctx.sb().append("]\n");
        } else if (value instanceof EnumValue enumVal) {
            ctx.sb().append(enumVal.getName());
        } else if (value instanceof VariableReference varRef) {
            ctx.sb().append('$').append(varRef.getName());
            ctx.addVariable(varRef.getName());
        } else {
            throw new IllegalArgumentException("Unsupported value " + value + " of type " + value.getClass().getName());
        }
    }

    private static String prepareString(final String str) {
        return str.replaceAll("\"", "\\\"").replaceAll("\n", "\\n");
    }
}
