package io.vetka.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public abstract class WebTestBase extends TestBase {

    @Autowired
    protected WebTestClient webClient;

    protected void addGraphQlEndpoint(final String schema) {
        assertNotNull(
                graphQlEndpointService.create(Map.of("name", "test", "address", "http://localhost", "schema", schema))
                        .block());
        assertTrue(
                Objects.requireNonNull(graphQlSchemaRegistryService.reloadSchemas().block()).getEndpoints().size() > 0);
    }
}
