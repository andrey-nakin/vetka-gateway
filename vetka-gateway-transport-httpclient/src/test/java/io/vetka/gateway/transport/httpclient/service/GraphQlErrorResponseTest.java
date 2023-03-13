package io.vetka.gateway.transport.httpclient.service;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.vetka.gateway.WiremockTestBase;
import io.vetka.gateway.test.GraphQlSchemaTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import io.vetka.gateway.util.HttpTestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GraphQlErrorResponseTest extends WiremockTestBase {

    @Autowired
    private HttpClientTransportService service;

    @Test
    @Disabled
    void basic() {
        final var url = "/graphql-error";
        final var info = graphQlEndpointInfo(url);

        wireMockServer.stubFor(WireMock.post(url)
                .willReturn(new ResponseDefinitionBuilder().withHeader("Content-Type", "application/json").withBody("""
                        {
                            "errors": [
                                {
                                    "message": "Message 1",
                                    "locations": [
                                        {
                                            "line": 1,
                                            "column": 2
                                        }
                                    ],
                                    "extensions": {
                                        "classification": "Classification 1"
                                    }
                                },
                                {
                                    "message": "Message 2",
                                    "locations": [
                                        {
                                            "line": 3,
                                            "column": 4
                                        }
                                    ],
                                    "extensions": {
                                        "classification": "Classification 2"
                                    }
                                }
                            ]
                        }""")));

        final var response =
                service.request(HttpTestUtils.nonEmptyHttpHeaders(), GraphQlSchemaTestUtils.simpleQuery(), info).join();
        assertNull(response.getData());
    }
}
