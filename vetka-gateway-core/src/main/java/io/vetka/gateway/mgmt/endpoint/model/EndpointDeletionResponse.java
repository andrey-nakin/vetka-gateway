package io.vetka.gateway.mgmt.endpoint.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointDeletionResponse implements EndpointDeletionPayload {

    @NonNull
    private final String id;
}
