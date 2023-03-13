package io.vetka.gateway.endpoint;

import io.vetka.gateway.WebTestBase;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.schema.service.GraphQlConstants;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

class ValuesInVariablesTest extends WebTestBase {

    @Test
    void basic() {
        addGraphQlEndpoint("type Query { a(b: String): String }");

        Mockito.when(transportService.request(Mockito.any(HttpHeaders.class), Mockito.any(String.class),
                Mockito.any(GraphQlEndpointInfo.class))).thenAnswer(invocation -> {
            final var query = invocation.getArgument(1, String.class);
            assertEquals(
                    "1{\"query\":\"query {\\na(\\nb: $b\\n)\\n {\\n__typename\\n}\\n}\\n\",\"variables\":{\"b\":\"c\"}}",
                    query);
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
