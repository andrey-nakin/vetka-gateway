package io.vetka.gateway.persistence.api.exception.endpoint;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EndpointNotFoundException extends RuntimeException {

    @NonNull
    @Getter
    private final String id;
}
