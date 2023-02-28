package io.vetka.gateway.schema.exception;

public class SchemaConflictException extends RuntimeException {

    public SchemaConflictException(String message) {
        super(message);
    }
}
