package io.vetka.gateway.graphql;

import graphql.language.ObjectTypeDefinition;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaProblem;
import io.vetka.gateway.schema.service.GraphQlConstants;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphQlSchemaMerger {

    public static TypeDefinitionRegistry merge(final Stream<String> sdls) throws SchemaProblem {
        final var typeDefinitionRegistry = new TypeDefinitionRegistry();
        sdls.forEachOrdered(sdl -> {
            final var schemaParser = new SchemaParser();
            final var sdlRegistry = schemaParser.parse(sdl);

            checkTypeConflict(typeDefinitionRegistry, sdlRegistry, GraphQlConstants.TYPE_QUERY);
            checkTypeConflict(typeDefinitionRegistry, sdlRegistry, GraphQlConstants.TYPE_MUTATION);
            checkTypeConflict(typeDefinitionRegistry, sdlRegistry, GraphQlConstants.TYPE_SUBSCRIPTION);

            typeDefinitionRegistry.merge(sdlRegistry);
        });
        return typeDefinitionRegistry;
    }

    private static void checkTypeConflict(final TypeDefinitionRegistry target, final TypeDefinitionRegistry source,
            final String typeName) {

        if (target.getType(typeName).isPresent() && source.getType(typeName).isPresent()) {
            final var dest = (ObjectTypeDefinition) target.getType(typeName).get();
            target.remove(dest);

            final var src = (ObjectTypeDefinition) source.getType(typeName).get();
            source.remove(src);

            target.add(ObjectTypeDefinition.newObjectTypeDefinition()
                    .name(dest.getName())
                    .directives(dest.getDirectives())
                    .fieldDefinitions(
                            Stream.concat(dest.getFieldDefinitions().stream(), src.getFieldDefinitions().stream())
                                    .toList())
                    .description(dest.getDescription())
                    .sourceLocation(dest.getSourceLocation())
                    .comments(dest.getComments())
                    .ignoredChars(dest.getIgnoredChars())
                    .additionalData(dest.getAdditionalData())
                    .build());
        }
    }
}
