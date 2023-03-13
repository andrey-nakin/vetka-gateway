package io.vetka.gateway.endpoint;

import io.vetka.gateway.ApplicationTest;
import io.vetka.gateway.transport.api.ITransportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ApplicationTest.class})
class ValuesInVariablesTest {

    @MockBean
    private ITransportService transportService;

    @Test
    void basic() {
        // TODO add the test code
    }
}
