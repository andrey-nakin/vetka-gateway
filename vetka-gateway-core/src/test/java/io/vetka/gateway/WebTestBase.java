package io.vetka.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public abstract class WebTestBase extends TestBase {

    @Autowired
    protected WebTestClient webClient;
}
