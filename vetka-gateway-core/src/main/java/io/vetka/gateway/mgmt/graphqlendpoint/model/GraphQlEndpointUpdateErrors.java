package io.vetka.gateway.mgmt.graphqlendpoint.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQlEndpointUpdateErrors implements GraphQlEndpointUpdatePayload {

    @NonNull
    private final List<GraphQlEndpointUpdateError> errors;
}
