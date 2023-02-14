package com.vetka.gateway.persistence.mongo.mapping.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateInput;
import com.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Lazy;

@Mapper
@Lazy
public abstract class GraphQlEndpointSerializer {

    public abstract GraphQlEndpoint toModel(GraphQlEndpointDocument src);

    @Mapping(target = "id", ignore = true)
    public abstract GraphQlEndpointDocument toDocument(GraphQlEndpointCreationInput src);

    public GraphQlEndpointDocument toDocument(final GraphQlEndpointUpdateInput src, final Set<String> updatableFields) {
        if (src == null) {
            return null;
        }

        GraphQlEndpointDocument graphQlEndpointDocument = new GraphQlEndpointDocument();

        if (updatableFields.contains("id")) {
            graphQlEndpointDocument.setId(src.getId());
        }
        if (updatableFields.contains("name")) {
            graphQlEndpointDocument.setName(src.getName());
        }
        if (updatableFields.contains("address")) {
            graphQlEndpointDocument.setAddress(src.getAddress());
        }
        if (updatableFields.contains("schema")) {
            graphQlEndpointDocument.setSchema(src.getSchema());
        }
        if (updatableFields.contains("httpVersion")) {
            graphQlEndpointDocument.setHttpVersion(src.getHttpVersion());
        }
        if (updatableFields.contains("connectTimeout")) {
            graphQlEndpointDocument.setConnectTimeout(src.getConnectTimeout());
        }
        if (updatableFields.contains("readTimeout")) {
            graphQlEndpointDocument.setReadTimeout(src.getReadTimeout());
        }

        return graphQlEndpointDocument;
    }
}
