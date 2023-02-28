package io.vetka.gateway.schema.service;

import graphql.TypeResolutionEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.TypeResolver;
import java.util.Map;

public class GraphQlPolymorphicTypeResolver implements TypeResolver {
    @Override
    public GraphQLObjectType getType(final TypeResolutionEnvironment env) {
        Object javaObject = env.getObject();
        if (javaObject instanceof Map<?, ?>) {
            return env.getSchema().getObjectType((String) ((Map<?, ?>) javaObject).get("__typename"));
        } else {
            throw new IllegalArgumentException("Unsupported java type: " + javaObject.getClass().getName());
        }
    }
}
