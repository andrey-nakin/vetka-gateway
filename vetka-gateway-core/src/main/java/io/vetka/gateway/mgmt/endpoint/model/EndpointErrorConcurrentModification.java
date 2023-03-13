package io.vetka.gateway.mgmt.endpoint.model;

import io.vetka.gateway.mgmt.error.model.IError;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateError;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointErrorConcurrentModification implements GraphQlEndpointUpdateError, IError {

    @NonNull
    private final String message;
    private final String id;
}
