package com.vetka.gateway.transport.httpclient.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
@ConfigurationProperties("vetka.gateway.transport.httpclient")
@Getter
@Setter
public class HttpClientProperties {

    private String httpVersion;
    private Integer connectTimeout;
    private Integer readTimeout;
}
