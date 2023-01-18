package com.vetka.gateway.persistence.api;

import com.vetka.gateway.persistence.mongo.service.MongoPersistenceServiceFacade;
import com.vetka.gateway.persistence.properties.PersistenceProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class PersistenceServiceFacade {

    private final MongoPersistenceServiceFacade mongoServiceFacade;
    private final PersistenceProperties properties;

    public IPersistenceServiceFacade serviceFacade() {
        return mongoServiceFacade;
    }

    @PostConstruct
    void init() {
        Assert.isTrue(
                StringUtils.isBlank(properties.getEngine()) || StringUtils.equalsAnyIgnoreCase(properties.getEngine(),
                        MongoPersistenceServiceFacade.NAME),
                "Unsupported persistence engine: " + properties.getEngine());
    }
}
