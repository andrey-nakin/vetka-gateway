package com.vetka.gateway.schema.bo;

import com.vetka.gateway.schema.exception.NoSchemaException;
import graphql.schema.GraphQLSchema;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GraphQlSchemaInfo {

    private final GraphQLSchema schema;

    @NonNull
    private final List<GraphQlEndpointInfo> endpoints;

    @NonNull
    public GraphQLSchema getSchema() throws NoSchemaException {
        if (schema == null) {
            throw new NoSchemaException();
        }
        return schema;
    }
}
