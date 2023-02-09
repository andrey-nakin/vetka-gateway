package com.vetka.gateway.endpoint.bo;

import java.util.Map;
import org.springframework.graphql.server.WebGraphQlRequest;

public record WebGraphQlRequestWrapper(WebGraphQlRequest request, Map<String, Object> body) {

}
