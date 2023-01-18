package com.vetka.gateway.persistence.mongo.service.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import com.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import com.vetka.gateway.persistence.mongo.mapping.graphqlendpoint.GraphQlEndpointSerializer;
import com.vetka.gateway.persistence.mongo.repository.graphqlendpoint.GraphQlEndpointRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoGraphQlEndpointService implements IGraphQlEndpointService {

    private final GraphQlEndpointSerializer serializer;
    private final GraphQlEndpointRepository repository;

    @Override
    public Flux<GraphQlEndpoint> findAll() {
        return repository.findAll().map(serializer::toModel);
    }

    @Override
    public Mono<GraphQlEndpoint> create(@NonNull final GraphQlEndpointCreationInput input) {
        log.info("create input={}", input);

        return repository.insert(serializer.toDocument(input)).map(serializer::toModel);
    }

    @Override
    public Mono<Void> delete(@NonNull final String id) {
        log.info("delete id={}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EndpointNotFoundException(id)))
                .flatMap(e -> repository.deleteById(id));
    }
}
