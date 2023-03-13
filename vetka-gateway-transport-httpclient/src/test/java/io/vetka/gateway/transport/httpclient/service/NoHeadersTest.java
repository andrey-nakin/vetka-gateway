package io.vetka.gateway.transport.httpclient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.vetka.gateway.WiremockTestBase;
import io.vetka.gateway.test.GraphQlSchemaTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

class NoHeadersTest extends WiremockTestBase {

    @Autowired
    private HttpClientTransportService service;

    @Test
    void basic() {
        final var url = "/graphql";
        final var info = graphQlEndpointInfo(url);

        wireMockServer.stubFor(WireMock.post(url)
                .willReturn(new ResponseDefinitionBuilder().withHeader("Content-Type", "application/json").withBody("""
                        {"data": true}""")));

        final var response = service.request(HttpHeaders.EMPTY, GraphQlSchemaTestUtils.simpleQuery(), info).join();
        assertNotNull(response.getData());
        assertEquals(true, response.getData());
    }
}
