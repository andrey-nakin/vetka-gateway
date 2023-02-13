package com.vetka.gateway.transport.api;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import com.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import java.util.concurrent.CompletionStage;
import org.springframework.graphql.GraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ITransportService {

    Mono<ServerResponse> proxyRequest(ServerRequest serverRequest, WebGraphQlRequestWrapper webGraphQlRequestWrapper,
            GraphQlEndpointInfo graphQlEndpointInfo);

    CompletionStage<GraphQlResponse> request(HttpHeaders httpHeaders, String query,
            GraphQlEndpointInfo graphQlEndpointInfo);
}
