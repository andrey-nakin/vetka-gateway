package com.vetka.gateway.schema.exception;

import graphql.GraphQLError;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;

public class BadSchemaException extends RuntimeException {

    @NonNull
    @Getter
    private final String schema;
    @NonNull
    @Getter
    private final List<GraphQLError> errors;

    public BadSchemaException(String message, Throwable cause, @NonNull String schema,
            @NonNull final List<GraphQLError> errors) {

        super(message, cause);
        this.schema = schema;
        this.errors = errors;
    }
}
