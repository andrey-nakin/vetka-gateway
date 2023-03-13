package io.vetka.gateway.endpoint;

import io.vetka.gateway.WebTestBase;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.schema.service.GraphQlConstants;
import io.vetka.gateway.schema.service.GraphQlSchemaRegistryService;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class ValuesInVariablesTest extends WebTestBase {

    @Autowired
    private GraphQlSchemaRegistryService graphQlSchemaRegistryService;

    @Test
    @Disabled
    void basic() {
        assertNotNull(graphQlEndpointService.create(
                        Map.of("name", "test", "address", "http://localhost", "schema", "type Query { a(b: String): String }"))
                .block());
        assertTrue(
                Objects.requireNonNull(graphQlSchemaRegistryService.reloadSchemas().block()).getEndpoints().size() > 0);

        Mockito.when(transportService.request(Mockito.any(HttpHeaders.class), Mockito.any(String.class),
                Mockito.any(GraphQlEndpointInfo.class))).thenAnswer(invocation -> {
            final var query = invocation.getArgument(1, String.class);
            return null;
        });

        webClient.post()
                .uri(GatewayRouter.DEFAULT_PATH)
                .contentType(GraphQlConstants.MEDIA_TYPE)
                .bodyValue(Map.of("query", "query q($b: String){ a(b: $b) }", "variables", Map.of("b", "c")))
                .exchange()
                .expectStatus()
                .isOk();
        // TODO add the test code
    }
}
