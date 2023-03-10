package io.vetka.gateway.transport.api.exception;

public class GraphQlTransportException extends RuntimeException {

    public GraphQlTransportException(String message) {
        super(message);
    }

    public GraphQlTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphQlTransportException(Throwable cause) {
        super(cause);
    }
}
