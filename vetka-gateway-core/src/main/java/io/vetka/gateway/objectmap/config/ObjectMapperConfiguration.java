package io.vetka.gateway.objectmap.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import graphql.ExecutionResult;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.language.SourceLocation;
import io.vetka.gateway.graphql.DefaultExecutionResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final var graphqlModule = new SimpleModule().addDeserializer(GraphQLError.class, new JsonDeserializer<>() {
            @Override
            @SuppressWarnings("unchecked")
            public GraphQLError deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                final var builder = GraphqlErrorBuilder.newError();
                final JsonNode tree = p.getCodec().readTree(p);

                final var message = tree.get("message");
                if (message != null) {
                    builder.message(message.textValue());
                }

                final var locations = tree.get("locations");
                if (locations != null && locations.isArray()) {
                    builder.locations(Arrays.asList(p.getCodec().treeToValue(locations, SourceLocation[].class)));
                } else {
                    builder.locations(null);
                }

                final var extensions = tree.get("extensions");
                if (extensions != null && extensions.isObject()) {
                    builder.extensions(p.getCodec().treeToValue(extensions, Map.class));
                }

                final var path = tree.get("path");
                if (path != null && path.isArray()) {
                    builder.path(Arrays.asList(p.getCodec().treeToValue(path, Object[].class)));
                }

                return builder.build();
            }
        }).addDeserializer(SourceLocation.class, new JsonDeserializer<>() {
            @Override
            public SourceLocation deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                final JsonNode tree = p.getCodec().readTree(p);
                final var line = tree.get("line");
                final var column = tree.get("column");
                final var sourceName = tree.get("sourceName");
                return new SourceLocation(line == null ? 0 : line.intValue(), column == null ? 0 : column.intValue(),
                        sourceName == null ? null : sourceName.textValue());
            }
        }).addDeserializer(ExecutionResult.class, new JsonDeserializer<>() {
            @Override
            @SuppressWarnings("unchecked")
            public ExecutionResult deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                final var builder = DefaultExecutionResult.builder();
                final JsonNode tree = p.getCodec().readTree(p);

                final var data = tree.get("data");
                if (data != null) {
                    builder.data(p.getCodec().treeToValue(data, Object.class));
                }

                final var errors = tree.get("errors");
                if (errors != null && errors.isArray()) {
                    builder.errors(Arrays.asList(p.getCodec().treeToValue(errors, GraphQLError[].class)));
                }

                final var extensions = tree.get("extensions");
                if (extensions != null && extensions.isObject()) {
                    builder.extensions(p.getCodec().treeToValue(extensions, Map.class));
                }

                return builder.build();
            }
        });

        final ObjectMapper result = new ObjectMapper();
        result.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
        result.registerModule(graphqlModule);
        return result;
    }
}
