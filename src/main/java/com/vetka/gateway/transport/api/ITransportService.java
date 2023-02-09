package com.vetka.gateway.transport.api;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ITransportService {

    Mono<ServerResponse> proxyRequest(ServerRequest serverRequest, WebGraphQlRequestWrapper webGraphQlRequestWrapper,
            GraphQlEndpoint graphQlEndpoint);
}