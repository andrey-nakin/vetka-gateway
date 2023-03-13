package io.vetka.gateway.persistence.exception.endpoint;

import io.vetka.gateway.persistence.exception.PersistenceException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConcurrentEndpointModificationException extends PersistenceException {

    @Getter
    private final String id;
}
