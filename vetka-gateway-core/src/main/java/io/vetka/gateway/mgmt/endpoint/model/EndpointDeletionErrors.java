package io.vetka.gateway.mgmt.endpoint.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointDeletionErrors implements EndpointDeletionResponse {

    @NonNull
    private final List<EndpointDeletionError> errors;
}
