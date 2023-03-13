package io.vetka.gateway;

import io.vetka.gateway.persistence.api.IGraphQlEndpointService;
import io.vetka.gateway.persistence.inmemory.service.InMemoryGraphQlEndpointService;
import io.vetka.gateway.transport.api.ITransportService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ApplicationTest.class})
public abstract class TestBase {

    @Autowired
    protected IGraphQlEndpointService graphQlEndpointService;

    @MockBean
    private ITransportService transportService;

    @BeforeEach
    void beforeTest() {
        if (graphQlEndpointService instanceof InMemoryGraphQlEndpointService e) {
            e.clear();
        }
    }
}
