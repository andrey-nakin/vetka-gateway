package io.vetka.gateway.persistence.mongo.service.graphqlendpoint;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointDuplicatingNameException;
import io.vetka.gateway.persistence.api.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.persistence.api.graphqlendpoint.IGraphQlEndpointService;
import io.vetka.gateway.persistence.mongo.mapping.graphqlendpoint.GraphQlEndpointSerializer;
import io.vetka.gateway.persistence.mongo.repository.graphqlendpoint.GraphQlEndpointRepository;
import java.util.Map;
import java.util.Objects;
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
    public Mono<GraphQlEndpoint> create(@NonNull final Map<String, Object> input) {
        log.info("create input={}", input);

        return checkName((String) input.get("name"), null).flatMap(
                unused -> repository.insert(serializer.toDocument(input)).map(serializer::toModel));
    }

    @Override
    public Mono<GraphQlEndpoint> update(@NonNull final Map<String, Object> input) {
        log.info("update input={}", input);

        final var id = (String) input.get("id");
        return checkName((String) input.get("name"), id).flatMap(unused -> repository.findById(id))
                .flatMap(existing -> repository.save(serializer.toDocument(existing, input)))
                .map(serializer::toModel);
    }

    @Override
    public Mono<Void> delete(@NonNull final String id) {
        log.info("delete id={}", id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EndpointNotFoundException(id)))
                .flatMap(e -> repository.deleteById(id));
    }

    private Mono<String> checkName(final String name, final String id) {
        return name == null ? Mono.empty() : repository.findAllByName(name).collectList().flatMap(l -> {
            if (l.stream().anyMatch(e -> !Objects.equals(e.getId(), id))) {
                return Mono.error(new EndpointDuplicatingNameException(name));
            } else {
                return Mono.just(name);
            }
        });
    }
}
