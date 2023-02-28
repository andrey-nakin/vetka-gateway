package io.vetka.gateway.persistence.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vetka.gateway.persistence")
@Getter
@Setter
public class PersistenceProperties {

    private String engine;
}
