package io.vetka.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class VetkaGatewayDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetkaGatewayDemoApplication.class, args);
    }
}
