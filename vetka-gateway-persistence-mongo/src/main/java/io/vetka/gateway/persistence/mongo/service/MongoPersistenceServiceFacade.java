package io.vetka.gateway.persistence.mongo.service;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import io.vetka.gateway.persistence.api.IPersistenceServiceFacade;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import io.vetka.gateway.persistence.mongo.mapping.graphqlendpoint.GraphQlEndpointSerializer;
import io.vetka.gateway.persistence.mongo.repository.graphqlendpoint.GraphQlEndpointRepository;
import io.vetka.gateway.persistence.mongo.service.graphqlendpoint.MongoGraphQlEndpointService;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MongoPersistenceServiceFacade implements IPersistenceServiceFacade {

    private final MongoGraphQlEndpointService mongoGraphQlEndpointService;

    public IGraphQlEndpointService graphQlEndpointService() {
        return mongoGraphQlEndpointService;
    }
}
