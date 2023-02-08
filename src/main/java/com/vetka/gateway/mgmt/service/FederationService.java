package com.vetka.gateway.mgmt.service;

import com.vetka.gateway.persistence.api.PersistenceServiceFacadeConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FederationService {

    private final PersistenceServiceFacadeConfiguration persistenceServiceFacadeConfiguration;

    public <T> Mono<T> reconfigure(final T context) {
        log.info("reconfigure");
        // TODO
        return Mono.just(context);
    }
}
