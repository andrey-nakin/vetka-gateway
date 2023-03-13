package io.vetka.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vetka.gateway.endpoint.GatewayRouter;
import io.vetka.gateway.schema.service.GraphQlConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public abstract class WebTestBase extends TestBase {

    @Autowired
    protected WebTestClient webClient;

    protected void addGraphQlEndpoint(final String schema) {
        assertNotNull(graphQlEndpointService.create(
                Map.of("name", UUID.randomUUID().toString(), "address", "http://localhost", "schema", schema)).block());
        assertTrue(
                Objects.requireNonNull(graphQlSchemaRegistryService.reloadSchemas().block()).getEndpoints().size() > 0);
    }

    protected WebTestClient.ResponseSpec graphQlRequest(final String query, final Object... variables) {
        final var varMap = new LinkedHashMap<String, Object>();
        for (int i = 0; i < variables.length - 1; i += 2) {
            varMap.put((String) variables[i], variables[i + 1]);
        }

        return webClient.post()
                .uri(GatewayRouter.DEFAULT_PATH)
                .contentType(GraphQlConstants.MEDIA_TYPE)
                .bodyValue(Map.of("query", query, "variables", varMap))
                .exchange();
    }
}
