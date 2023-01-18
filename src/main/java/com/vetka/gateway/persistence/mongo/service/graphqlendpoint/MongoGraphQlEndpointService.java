package com.vetka.gateway.persistence.mongo.service.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import com.vetka.gateway.persistence.mongo.mapping.graphqlendpoint.GraphQlEndpointSerializer;
import com.vetka.gateway.persistence.mongo.repository.graphqlendpoint.GraphQlEndpointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MongoGraphQlEndpointService implements IGraphQlEndpointService {

    private final GraphQlEndpointSerializer serializer;
    private final GraphQlEndpointRepository repository;

    public Flux<GraphQlEndpoint> findAll() {
        return repository.findAll().map(serializer::toModel);
    }
}
