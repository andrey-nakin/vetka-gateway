package io.vetka.gateway.endpoint;

import io.vetka.gateway.WebTestBase;
import io.vetka.gateway.schema.service.GraphQlConstants;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ValuesInVariablesTest extends WebTestBase {

    @Test
    @Disabled
    void basic() {
        webClient.post()
                .uri(GatewayRouter.DEFAULT_PATH)
                .contentType(GraphQlConstants.MEDIA_TYPE)
                .bodyValue(Map.of("query", "{ a b }"))
                .exchange()
                .expectStatus()
                .isOk();
        // TODO add the test code
    }
}
