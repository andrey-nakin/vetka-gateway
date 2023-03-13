package io.vetka.gateway.persistence.inmemory.service;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import io.vetka.gateway.persistence.exception.endpoint.DuplicatingEndpointNameException;
import io.vetka.gateway.persistence.exception.endpoint.EndpointNotFoundException;
import io.vetka.gateway.persistence.inmemory.mapping.InMemoryGraphQlEndpointMapper;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class InMemoryGraphQlEndpointService implements IGraphQlEndpointService {

    private final ConcurrentHashMap<String, GraphQlEndpoint> storage = new ConcurrentHashMap<>();

    private final InMemoryGraphQlEndpointMapper mapper;

    @Override
    public Flux<GraphQlEndpoint> findAll() {
        return Flux.fromStream(storage.values().stream().map(mapper::toModel));
    }

    @Override
    public synchronized Mono<GraphQlEndpoint> create(@NonNull final Map<String, Object> input) {
        final var builder = mapper.toEntity(GraphQlEndpoint.builder(), input);
        builder.id(UUID.randomUUID().toString());
        final var e = builder.build();
        if (exists(null, e.getName())) {
            return Mono.error(new DuplicatingEndpointNameException(e.getName()));
        }

        storage.put(e.getId(), e);
        return Mono.just(mapper.toModel(e));
    }

    @Override
    public synchronized Mono<GraphQlEndpoint> update(Map<String, Object> input) {
        final var id = (String) input.get("id");
        if (!storage.containsKey(id)) {
            return Mono.error(new EndpointNotFoundException(id));
        }

        final var e = mapper.toEntity(storage.get(id).toBuilder(), input).build();
        if (exists(e.getId(), e.getName())) {
            return Mono.error(new DuplicatingEndpointNameException(e.getName()));
        }

        storage.put(e.getId(), e);
        return Mono.just(mapper.toModel(e));
    }

    @Override
    public synchronized Mono<Void> delete(String id) {
        if (!storage.containsKey(id)) {
            return Mono.error(new EndpointNotFoundException(id));
        }

        storage.remove(id);
        return Mono.create(s -> {});
    }

    public void clear() {
        storage.clear();
    }

    private boolean exists(final String id, final String name) {
        return storage.values()
                .stream()
                .anyMatch(e -> Objects.equals(e.getName(), name) && !Objects.equals(e.getId(), id));
    }
}
