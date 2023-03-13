package io.vetka.gateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.test.GraphQlSchemaTestUtils;
import java.util.UUID;
import lombok.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

@SpringBootTest(classes = {ApplicationTest.class})
@AutoConfigureWireMock(port = 0)
public abstract class WiremockTestBase {

    @Autowired
    protected WireMockServer wireMockServer;
    @Value("${wiremock.server.port}")
    protected Integer wireMockPort;

    @MockBean
    protected IGraphQlEndpointService iGraphQlEndpointService;

    @AfterEach
    void after() {
        wireMockServer.resetAll();
    }

    protected String wiremockUrl(@NonNull final String query) {
        return "http://localhost:" + wireMockPort + query;
    }

    protected GraphQlEndpointInfo graphQlEndpointInfo(@NonNull final String query) {
        final var id = UUID.randomUUID().toString();
        return GraphQlEndpointInfo.builder()
                .graphQlEndpoint(GraphQlEndpoint.builder()
                        .id(id)
                        .name(id)
                        .address(wiremockUrl(query))
                        .schema(GraphQlSchemaTestUtils.simpleSdl())
                        .build())
                .schema(GraphQlSchemaTestUtils.simpleSchema())
                .build();
    }
}
