package io.vetka.gateway.graphql;

import graphql.ExecutionResult;
import graphql.GraphQLError;

import static graphql.collect.ImmutableKit.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
public class DefaultExecutionResult implements ExecutionResult {

    private List<GraphQLError> errors;
    private Object data;
    private Map<Object, Object> extensions;

    @Override
    public List<GraphQLError> getErrors() {
        return errors;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) data;
    }

    @Override
    public boolean isDataPresent() {
        return data != null;
    }

    @Override
    public Map<Object, Object> getExtensions() {
        return extensions;
    }

    @Override
    public Map<String, Object> toSpecification() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (errors != null && !errors.isEmpty()) {
            result.put("errors", errorsToSpec(errors));
        }
        if (isDataPresent()) {
            result.put("data", data);
        }
        if (extensions != null) {
            result.put("extensions", extensions);
        }
        return result;
    }

    private Object errorsToSpec(List<GraphQLError> errors) {
        return map(errors, GraphQLError::toSpecification);
    }
}
