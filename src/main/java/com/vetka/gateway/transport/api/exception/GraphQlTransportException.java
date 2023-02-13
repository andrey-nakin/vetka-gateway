package com.vetka.gateway.transport.api.exception;

public abstract class GraphQlTransportException extends RuntimeException {

    public GraphQlTransportException(String message) {
        super(message);
    }

    public GraphQlTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
