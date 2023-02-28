package io.vetka.gateway.persistence.mongo.repository.graphqlendpoint;

import io.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@Lazy
public interface GraphQlEndpointRepository extends ReactiveMongoRepository<GraphQlEndpointDocument, String> {

    Flux<GraphQlEndpointDocument> findAllByName(String name);
}
