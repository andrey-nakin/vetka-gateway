package com.vetka.gateway.persistence.api;

import com.vetka.gateway.persistence.mongo.service.MongoPersistenceServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersistenceServiceFacade {

    private final MongoPersistenceServiceFacade mongoServiceFacade;

    public IPersistenceServiceFacade serviceFacade() {
        return mongoServiceFacade;
    }
}
