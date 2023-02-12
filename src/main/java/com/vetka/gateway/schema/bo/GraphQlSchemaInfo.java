package com.vetka.gateway.schema.bo;

import graphql.schema.GraphQLSchema;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GraphQlSchemaInfo {

    @NonNull
    private final GraphQLSchema schema;

    @NonNull
    private final List<GraphQlEndpointInfo> endpoints;
}
