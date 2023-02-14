package com.vetka.gateway.mgmt.endpoint.model;

import com.vetka.gateway.mgmt.error.model.IError;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationError;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointUpdateError;
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
