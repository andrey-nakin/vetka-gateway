package io.vetka.gateway.transport.httpclient.service;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.vetka.gateway.WiremockTestBase;
import io.vetka.gateway.test.GraphQlSchemaTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import io.vetka.gateway.util.HttpTestUtils;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GraphQlErrorResponseTest extends WiremockTestBase {

    @Autowired
    private HttpClientTransportService service;

    @Test
    @SuppressWarnings("unchecked")
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
                                            "column": 2,
                                            "sourceName": "source name 1"
                                        },
                                        {
                                            "line": 3,
                                            "column": 4
                                        }
                                    ],
                                    "extensions": {
                                        "a": "b",
                                        "c" : {
                                            "d": "e"
                                        }
                                    },
                                    "path": [
                                        "a", 1, false
                                    ]
                                },
                                {
                                    "message": "Message 2",
                                    "locations": [
                                        {
                                            "line": 5,
                                            "column": 6,
                                            "sourceName": null
                                        }
                                    ],
                                    "path": []
                                },
                                {
                                    "message": "Message 3"
                                }
                            ]
                        }""")));

        final var response =
                service.request(HttpTestUtils.nonEmptyHttpHeaders(), GraphQlSchemaTestUtils.simpleQuery(), info).join();
        assertNull(response.getData());
        assertNotNull(response.getErrors());
        assertEquals(3, response.getErrors().size());

        {
            final var e = response.getErrors().get(0);
            assertEquals("Message 1", e.getMessage());

            assertNotNull(e.getLocations());
            assertEquals(2, e.getLocations().size());
            assertEquals(1, e.getLocations().get(0).getLine());
            assertEquals(2, e.getLocations().get(0).getColumn());
            assertEquals("source name 1", e.getLocations().get(0).getSourceName());
            assertEquals(3, e.getLocations().get(1).getLine());
            assertEquals(4, e.getLocations().get(1).getColumn());
            assertNull(e.getLocations().get(1).getSourceName());

            assertNotNull(e.getExtensions());
            assertEquals("b", e.getExtensions().get("a"));
            assertNotNull(e.getExtensions().get("c"));
            assertEquals("e", ((Map<String, Object>) e.getExtensions().get("c")).get("d"));

            assertNotNull(e.getPath());
            assertEquals(3, e.getPath().size());
            assertEquals("a", e.getPath().get(0));
            assertEquals(1, e.getPath().get(1));
            assertEquals(false, e.getPath().get(2));
        }

        {
            final var e = response.getErrors().get(1);
            assertEquals("Message 2", e.getMessage());

            assertNotNull(e.getLocations());
            assertEquals(1, e.getLocations().size());
            assertEquals(5, e.getLocations().get(0).getLine());
            assertEquals(6, e.getLocations().get(0).getColumn());
            assertNull(e.getLocations().get(0).getSourceName());

            assertNull(e.getExtensions());

            assertNotNull(e.getPath());
            assertEquals(0, e.getPath().size());
        }

        {
            final var e = response.getErrors().get(2);
            assertEquals("Message 3", e.getMessage());
            assertNull(e.getLocations());
            assertNull(e.getExtensions());
            assertNull(e.getPath());
        }
    }
}
