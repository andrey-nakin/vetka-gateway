package com.vetka.gateway.schema.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadSchemaException extends RuntimeException {

    @NonNull
    @Getter
    private final String schema;
}
