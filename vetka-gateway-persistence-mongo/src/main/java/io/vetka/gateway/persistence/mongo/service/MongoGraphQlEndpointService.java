package io.vetka.gateway.persistence.mongo.service;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import io.vetka.gateway.persistence.exception.endpoint.DuplicatingEndpointNameException;
import io.vetka.gateway.persistence.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import io.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import io.vetka.gateway.persistence.mongo.mapping.graphqlendpoint.GraphQlEndpointSerializer;
import io.vetka.gateway.persistence.mongo.repository.graphqlendpoint.GraphQlEndpointRepository;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Primary
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
    public Mono<GraphQlEndpoint> create(@NonNull final Map<String, Object> input) {
        log.info("create input={}", input);

        final var doc = serializer.toDocument(input);
        return repository.insert(doc).map(serializer::toModel).onErrorMap(ex -> {
            if (ex instanceof DuplicateKeyException) {
                return new DuplicatingEndpointNameException(doc.getName());
            } else {
                return ex;
            }
        });
    }

    @Override
    public Mono<GraphQlEndpoint> update(@NonNull final Map<String, Object> input) {
        log.info("update input={}", input);

        final var id = (String) input.get("id");
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EndpointNotFoundException(id)))
                .map(existing -> serializer.toDocument(existing, input))
                .flatMap(this::update);
    }

    @Override
    public Mono<Void> delete(@NonNull final String id) {
        log.info("delete id={}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EndpointNotFoundException(id)))
                .flatMap(e -> repository.deleteById(id));
    }

    private Mono<GraphQlEndpoint> update(@NonNull final GraphQlEndpointDocument doc) {
        return repository.save(doc).map(serializer::toModel).onErrorMap(ex -> {
            if (ex instanceof DuplicateKeyException) {
                return new DuplicatingEndpointNameException(doc.getName());
            } else if (ex instanceof OptimisticLockingFailureException) {
                return new EndpointNotFoundException(doc.getId());
            } else {
                return ex;
            }
        });
    }
}
