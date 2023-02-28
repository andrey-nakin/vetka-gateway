package io.vetka.gateway.persistence.api.exception.endpoint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EndpointDuplicatingNameException extends RuntimeException {

    @Getter
    private final String name;
}
