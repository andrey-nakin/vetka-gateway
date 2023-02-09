package com.vetka.gateway.objectmap.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper result = new ObjectMapper();
        result.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
        return result;
    }
}
