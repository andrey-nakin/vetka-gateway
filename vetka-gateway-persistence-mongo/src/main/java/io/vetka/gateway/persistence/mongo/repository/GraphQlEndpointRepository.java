package io.vetka.gateway.persistence.mongo.repository;

import io.vetka.gateway.persistence.mongo.document.GraphQlEndpointDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphQlEndpointRepository extends ReactiveMongoRepository<GraphQlEndpointDocument, String> {

}
