package io.vetka.gateway.mgmt.graphqlendpoint.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointCreationErrors implements GraphQlEndpointCreationResponse {

    @NonNull
    private final List<GraphQlEndpointCreationError> errors;
}
