package com.vetka.gateway.mgmt.graphqlendpoint.model;

import com.vetka.gateway.mgmt.common.model.GraphQLError;
import com.vetka.gateway.mgmt.error.model.IError;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointErrorBadSchema implements GraphQlEndpointCreationError, GraphQlEndpointUpdateError, IError {

    @NonNull
    private final String message;
    @NonNull
    private final String schema;
    @NonNull
    private final List<GraphQLError> errors;
}
