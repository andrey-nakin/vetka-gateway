package com.vetka.gateway.endpoint.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("vetka.gateway")
@Getter
@Setter
public class GatewayProperties {

    private String path;
}
