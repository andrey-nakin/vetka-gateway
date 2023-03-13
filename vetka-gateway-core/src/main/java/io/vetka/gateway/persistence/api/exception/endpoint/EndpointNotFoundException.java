package io.vetka.gateway.persistence.api.exception.endpoint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EndpointNotFoundException extends RuntimeException {

    @Getter
    private final String id;
}
