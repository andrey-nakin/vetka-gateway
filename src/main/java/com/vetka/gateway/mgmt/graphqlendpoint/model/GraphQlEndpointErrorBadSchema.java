package com.vetka.gateway.mgmt.graphqlendpoint.model;

import com.vetka.gateway.mgmt.error.model.IError;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointErrorBadSchema implements GraphQlEndpointCreationError, IError {

    @NonNull
    private final String message;
    @NonNull
    private final String schema;
}
