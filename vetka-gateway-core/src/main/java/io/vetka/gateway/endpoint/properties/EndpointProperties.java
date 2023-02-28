package io.vetka.gateway.endpoint.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vetka.gateway.endpoint")
@Getter
@Setter
public class EndpointProperties {

    private String path;
}
