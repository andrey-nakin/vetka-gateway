package com.vetka.gateway.persistence.api;

import com.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import com.vetka.gateway.persistence.mongo.service.graphqlendpoint.MongoGraphQlEndpointService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceServiceFacadeConfiguration {

    @Bean
    @ConditionalOnProperty(value = "vetka.gateway.persistence.engine", havingValue = "mongo", matchIfMissing = true)
    public IPersistenceServiceFacade mongoPersistenceServiceFacade(
            final MongoGraphQlEndpointService mongoGraphQlEndpointService) {

        return new MongoPersistenceServiceFacade(mongoGraphQlEndpointService);
    }

    @RequiredArgsConstructor
    private record MongoPersistenceServiceFacade(
            MongoGraphQlEndpointService mongoGraphQlEndpointService) implements IPersistenceServiceFacade {

        public IGraphQlEndpointService graphQlEndpointService() {
            return mongoGraphQlEndpointService;
        }
    }
}
