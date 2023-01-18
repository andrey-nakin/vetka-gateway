package com.vetka.gateway.persistence.mongo.document.graphqlendpoint;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class GraphQlEndpointDocument {

    @Id
    private String id;
    private String name;
    @NonNull
    private String address;
    @NonNull
    private String schema;
}
