package io.vetka.gateway.persistence.exception.endpoint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EndpointNotFoundException extends RuntimeException {

    @Getter
    private final String id;
}
