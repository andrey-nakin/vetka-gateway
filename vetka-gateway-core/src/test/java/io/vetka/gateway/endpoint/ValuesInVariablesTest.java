package io.vetka.gateway.endpoint;

import io.vetka.gateway.WebTestBase;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;

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
                    "{\"query\":\"query {\\na(\\nb: $b\\n)\\n {\\n__typename\\n}\\n}\\n\",\"variables\":{\"b\":\"c\"}}",
                    query);
            return null;
        });

        graphQlRequest("query q($b: String){ a(b: $b) }", "b", "c");
        // TODO add the test code
    }
}
