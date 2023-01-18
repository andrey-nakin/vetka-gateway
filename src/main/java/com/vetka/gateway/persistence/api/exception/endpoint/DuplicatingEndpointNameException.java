package com.vetka.gateway.persistence.api.exception.endpoint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DuplicatingEndpointNameException extends RuntimeException {

    @Getter
    private final String name;
}
