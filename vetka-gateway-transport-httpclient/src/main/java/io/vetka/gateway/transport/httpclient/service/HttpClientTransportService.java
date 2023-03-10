package io.vetka.gateway.transport.httpclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import graphql.ExecutionResult;
import io.vetka.gateway.endpoint.bo.WebGraphQlRequestWrapper;
import io.vetka.gateway.objectmap.service.ObjectMapperHelper;
import io.vetka.gateway.schema.bo.GraphQlEndpointInfo;
import io.vetka.gateway.schema.service.GraphQlConstants;
import io.vetka.gateway.transport.api.ITransportService;
import io.vetka.gateway.transport.exception.GraphQlHttpErrorException;
import io.vetka.gateway.transport.exception.GraphQlJsonMappingException;
import io.vetka.gateway.transport.exception.GraphQlJsonProcessingException;
import io.vetka.gateway.transport.exception.GraphQlTransportException;
import io.vetka.gateway.transport.httpclient.properties.HttpClientProperties;
import io.vetka.gateway.util.HttpUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class HttpClientTransportService implements ITransportService {

    private static final HttpClient.Version DEFAULT_HTTP_VERSION = HttpClient.Version.HTTP_1_1;
    private static final Integer DEFAULT_CONNECT_TIMEOUT = 30;
    private static final Integer DEFAULT_READ_TIMEOUT = 600;
    private static final String[] UNTRANSMITTABLE_HTTP_HEADERS = new String[]{
            "Content-Length", "Content-Type", "Connection", "Host", "Accept", "Accept-Encoding"
    };

    private final HttpClientProperties properties;
    private final ObjectMapperHelper objectMapperHelper;

    public Mono<ServerResponse> proxyRequest(@NonNull final ServerRequest serverRequest,
            @NonNull final WebGraphQlRequestWrapper webGraphQlRequestWrapper,
            @NonNull final GraphQlEndpointInfo graphQlEndpointInfo) {

        if (log.isDebugEnabled()) {
            log.debug("proxying request to {}", graphQlEndpointInfo.getGraphQlEndpoint().getAddress());
        }

        try {
            final var request = HttpRequest.newBuilder()
                    .uri(URI.create(graphQlEndpointInfo.getGraphQlEndpoint().getAddress()))
                    .timeout(Duration.ofSeconds(validateReadTimeout(
                            ObjectUtils.defaultIfNull(graphQlEndpointInfo.getGraphQlEndpoint().getReadTimeout(),
                                    properties.getReadTimeout()))))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapperHelper.getObjectMapper()
                            .writeValueAsString(
                                    webGraphQlRequestWrapper.body())))  //  TODO: inefficient on long requests
                    .headers(headers(serverRequest.headers()))
                    .header("Accept", GraphQlConstants.MEDIA_TYPE.toString())
                    .header("Content-Type", GraphQlConstants.MEDIA_TYPE.toString())
                    .build();

            final var client = httpClient(graphQlEndpointInfo);
            return Mono.fromFuture(client.sendAsync(request,
                            HttpResponse.BodyHandlers.ofString())) //  TODO: inefficient on long responses
                    .flatMap(httpResponse -> ServerResponse.status(httpResponse.statusCode())
                            .headers(headers -> headers.putAll(httpResponse.headers().map()))
                            .bodyValue(httpResponse.body()));
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    @Override
    public CompletableFuture<ExecutionResult> request(final HttpHeaders httpHeaders, final String query,
            final GraphQlEndpointInfo graphQlEndpointInfo) {

        if (log.isDebugEnabled()) {
            log.debug("Sending request to {}", graphQlEndpointInfo.getGraphQlEndpoint().getAddress());
        }

        final var request = HttpRequest.newBuilder()
                .uri(URI.create(graphQlEndpointInfo.getGraphQlEndpoint().getAddress()))
                .timeout(Duration.ofSeconds(validateReadTimeout(
                        ObjectUtils.defaultIfNull(graphQlEndpointInfo.getGraphQlEndpoint().getReadTimeout(),
                                properties.getReadTimeout()))))
                .POST(HttpRequest.BodyPublishers.ofString(query));
        final var headers = headers(httpHeaders);
        if (headers.length > 0) {
            request.headers(headers);
        }
        request.header("Accept", GraphQlConstants.MEDIA_TYPE.toString())
                .header("Content-Type", GraphQlConstants.MEDIA_TYPE.toString());

        final var client = httpClient(graphQlEndpointInfo);
        return client.sendAsync(request.build(), HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(httpResponse -> parseGraphQlResponse(httpResponse, graphQlEndpointInfo));
    }

    private ExecutionResult parseGraphQlResponse(final HttpResponse<InputStream> httpResponse,
            final GraphQlEndpointInfo graphQlEndpointInfo) {

        if (log.isDebugEnabled()) {
            log.debug("Reading response from {}", graphQlEndpointInfo.getGraphQlEndpoint().getAddress());
        }

        if (httpResponse.statusCode() != HttpStatus.OK.value()) {
            throw new GraphQlHttpErrorException(httpResponse.statusCode(), graphQlEndpointInfo);
        }

        try {
            return objectMapperHelper.getObjectMapper()
                    .readValue(new BufferedReader(
                                    new InputStreamReader(httpResponse.body(), charsetFrom(httpResponse.headers()))),
                            ExecutionResult.class);
        } catch (JsonMappingException ex) {
            throw new GraphQlJsonMappingException(ex, graphQlEndpointInfo);
        } catch (JsonProcessingException ex) {
            throw new GraphQlJsonProcessingException(ex, graphQlEndpointInfo);
        } catch (IOException ex) {
            throw new GraphQlTransportException(ex);
        }
    }

    private static Charset charsetFrom(java.net.http.HttpHeaders headers) {
        return headers.firstValue("Content-type").flatMap(HttpUtils::contentTypeCharset).orElse(StandardCharsets.UTF_8);
    }

    private static String[] headers(final ServerRequest.Headers headers) {
        return headers(headers.asHttpHeaders());
    }

    private static String[] headers(final HttpHeaders httpHeaders) {
        final List<String> result = new ArrayList<>(httpHeaders.size());
        httpHeaders.forEach((headerName, headerValues) -> headerValues.forEach(value -> {
            if (!StringUtils.equalsAnyIgnoreCase(headerName, UNTRANSMITTABLE_HTTP_HEADERS)) {
                result.add(headerName);
                result.add(value);
            }
        }));
        return result.toArray(new String[0]);
    }

    private HttpClient httpClient(final GraphQlEndpointInfo graphQlEndpointInfo) {
        final var graphQlEndpoint = graphQlEndpointInfo.getGraphQlEndpoint();
        return graphQlEndpointInfo.getCachedValue("HttpClientTransportService.HttpClient", () -> HttpClient.newBuilder()
                .version(validateHttpVersion(
                        ObjectUtils.defaultIfNull(graphQlEndpoint.getHttpVersion(), properties.getHttpVersion())))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(validateConnectTimeout(
                        ObjectUtils.defaultIfNull(graphQlEndpoint.getConnectTimeout(),
                                properties.getConnectTimeout()))))
                .build());
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
