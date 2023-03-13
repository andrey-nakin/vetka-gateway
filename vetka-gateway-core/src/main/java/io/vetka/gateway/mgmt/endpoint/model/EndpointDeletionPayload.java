package io.vetka.gateway.mgmt.endpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointDeletionPayload implements EndpointDeletionResponse {

    @NonNull
    private final String id;
}
