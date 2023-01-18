package com.vetka.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class VetkaGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(VetkaGatewayApplication.class, args);
	}

}
