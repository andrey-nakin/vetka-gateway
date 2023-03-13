package io.vetka.gateway.test;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaGenerator;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphQlSchemaTestUtils {

    public static String simpleSdl() {
        return "type Query { mock: String }";
    }

    public static GraphQLSchema simpleSchema() {
        return SchemaGenerator.createdMockedSchema(simpleSdl());
    }

    public static String simpleQuery() {
        return "{ mock }";
    }
}
