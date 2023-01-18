package com.vetka.gateway.persistence.mongo.mapping.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import org.mapstruct.Mapper;

@Mapper
public abstract class GraphQlEndpointSerializer {

    public abstract GraphQlEndpoint toModel(GraphQlEndpointDocument src);
}
