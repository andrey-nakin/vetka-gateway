package io.vetka.gateway.mgmt.endpoint.model;

import io.vetka.gateway.mgmt.error.model.IError;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationError;
import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateError;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class EndpointErrorEmptyName implements IError, GraphQlEndpointCreationError, GraphQlEndpointUpdateError {

    @NonNull
    private final String message;
    @NonNull
    private final String name;
}
