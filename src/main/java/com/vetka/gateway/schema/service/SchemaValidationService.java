package com.vetka.gateway.schema.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SchemaValidationService {

    public Mono<String> validate(@NonNull final String schema) {
        log.info("validate schema={}", schema);
        // TODO
        return Mono.just(schema);
    }
}
