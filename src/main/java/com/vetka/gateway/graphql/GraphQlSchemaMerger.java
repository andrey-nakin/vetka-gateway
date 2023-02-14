package com.vetka.gateway.graphql;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaProblem;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphQlSchemaMerger {

    public static TypeDefinitionRegistry merge(final Stream<String> sdls) throws SchemaProblem {
        final var typeDefinitionRegistry = new TypeDefinitionRegistry();
        sdls.forEachOrdered(sdl -> {
            final var schemaParser = new SchemaParser();
            final var sdlRegistry = schemaParser.parse(sdl);
            typeDefinitionRegistry.merge(sdlRegistry);
        });
        return typeDefinitionRegistry;
    }
}
