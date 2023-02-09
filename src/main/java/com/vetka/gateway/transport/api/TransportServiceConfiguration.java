package com.vetka.gateway.transport.api;

import com.vetka.gateway.objectmap.service.ObjectMapperHelper;
import com.vetka.gateway.transport.httpclient.properties.HttpClientProperties;
import com.vetka.gateway.transport.httpclient.service.HttpClientTransportService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransportServiceConfiguration {

    @Bean
    @ConditionalOnProperty(value = "vetka.gateway.transport.engine", havingValue = "httpclient", matchIfMissing = true)
    public ITransportService httpClientTransportService(final HttpClientProperties properties,
            final ObjectMapperHelper objectMapperHelper) {

        return new HttpClientTransportService(properties, objectMapperHelper);
    }
}
