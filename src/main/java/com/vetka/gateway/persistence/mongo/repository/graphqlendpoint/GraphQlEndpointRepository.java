package com.vetka.gateway.persistence.mongo.repository.graphqlendpoint;

import com.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GraphQlEndpointRepository extends ReactiveMongoRepository<GraphQlEndpointDocument, String> {}
