package com.vetka.gateway.mgmt.endpoint.model;

import com.vetka.gateway.mgmt.error.model.IError;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointErrorUnknownId implements EndpointDeletionError, IError {

    @NonNull
    private final String message;
    @NonNull
    private final String id;
}
