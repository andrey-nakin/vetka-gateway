package io.vetka.gateway.schema.service;

import org.springframework.http.MediaType;

public interface GraphQlConstants {

    String TYPE_QUERY = "Query";
    String TYPE_MUTATION = "Mutation";
    String TYPE_SUBSCRIPTION = "Subscription";

    MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON;
}
