package io.vetka.gateway.endpoint;

import io.vetka.gateway.WebTestBase;
import io.vetka.gateway.schema.service.GraphQlConstants;
import io.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ValuesInVariablesTest extends WebTestBase {

    @Autowired
    private GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    @Test
    void basic() {
        assertNotNull(graphQlEndpointService.create(
                Map.of("name", "test", "address", "http://localhost", "schema", "type Query { a: String }")).block());
        assertTrue(
                Objects.requireNonNull(graphQlSchemaRegistryService.reloadSchemas().block()).getEndpoints().size() > 0);

        webClient.post()
                .uri(GatewayRouter.DEFAULT_PATH)
                .contentType(GraphQlConstants.MEDIA_TYPE)
                .bodyValue(Map.of("query", "{ a }"))
                .exchange()
                .expectStatus()
                .isOk();
        // TODO add the test code
    }
}
