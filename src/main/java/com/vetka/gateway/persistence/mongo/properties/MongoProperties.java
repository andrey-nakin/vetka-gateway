package com.vetka.gateway.persistence.mongo.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@ConfigurationProperties("vetka.gateway.persistence.mongo")
@Getter
@Setter
public class MongoProperties {

    private String databaseName;
}
