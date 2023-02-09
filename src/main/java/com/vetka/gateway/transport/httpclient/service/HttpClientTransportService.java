package com.vetka.gateway.transport.httpclient.service;

import com.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.objectmap.service.ObjectMapperHelper;
import com.vetka.gateway.transport.api.ITransportService;
import com.vetka.gateway.transport.httpclient.properties.HttpClientProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class HttpClientTransportService implements ITransportService {

    private static final HttpClient.Version DEFAULT_HTTP_VERSION = HttpClient.Version.HTTP_1_1;
    private static final Integer DEFAULT_CONNECT_TIMEOUT = 30;
    private static final Integer DEFAULT_READ_TIMEOUT = 600;

    private final HttpClientProperties properties;
    private final ObjectMapperHelper objectMapperHelper;

    public Mono<ServerResponse> proxyRequest(@NonNull final ServerRequest serverRequest,
            @NonNull final WebGraphQlRequestWrapper webGraphQlRequestWrapper,
            @NonNull final GraphQlEndpoint graphQlEndpoint) {

        log.debug("proxying request to {}", graphQlEndpoint.getAddress());

        final var client = HttpClient.newBuilder()
                .version(validateHttpVersion(
                        ObjectUtils.defaultIfNull(graphQlEndpoint.getHttpVersion(), properties.getHttpVersion())))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(validateConnectTimeout(
                        ObjectUtils.defaultIfNull(graphQlEndpoint.getConnectTimeout(),
                                properties.getConnectTimeout()))))
                .build();

        try {
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(graphQlEndpoint.getAddress()))
                    .timeout(Duration.ofSeconds(validateReadTimeout(
                            ObjectUtils.defaultIfNull(graphQlEndpoint.getReadTimeout(), properties.getReadTimeout()))))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapperHelper.getObjectMapper()
                            .writeValueAsString(
                                    webGraphQlRequestWrapper.body())))  //  TODO: inefficient on long requests
                    .header("Content-Type", "application/json");
            serverRequest.headers()
                    .asHttpHeaders()
                    .forEach((headerName, headerValues) -> headerValues.forEach(value -> {
                        if (!StringUtils.equalsAnyIgnoreCase(headerName, "Content-Length", "Content-Type",
                                "Accept-Encoding", "Connection")) {
                            request.header(headerName, value);
                        }
                    }));

            return Mono.fromFuture(client.sendAsync(request.build(),
                            HttpResponse.BodyHandlers.ofString())) //  TODO: inefficient on long responses
                    .flatMap(httpResponse -> ServerResponse.status(httpResponse.statusCode())
                            .headers(headers -> headers.putAll(httpResponse.headers().map()))
                            .bodyValue(httpResponse.body()));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    @PostConstruct
    void init() {
        System.getProperties().setProperty("jdk.httpclient.allowRestrictedHeaders", "Host");
    }

    private static Integer validateConnectTimeout(final Integer connectTimeout) {
        return connectTimeout != null && connectTimeout > 0 ? connectTimeout : DEFAULT_CONNECT_TIMEOUT;
    }

    private static Integer validateReadTimeout(final Integer readTimeout) {
        return readTimeout != null && readTimeout > 0 ? readTimeout : DEFAULT_READ_TIMEOUT;
    }

    private static HttpClient.Version validateHttpVersion(final String version) {
        if (version != null) {
            switch (version) {
                case "1.1" -> {
                    return HttpClient.Version.HTTP_1_1;
                }
                case "2" -> {
                    return HttpClient.Version.HTTP_2;
                }
            }
        }
        return DEFAULT_HTTP_VERSION;
    }
}