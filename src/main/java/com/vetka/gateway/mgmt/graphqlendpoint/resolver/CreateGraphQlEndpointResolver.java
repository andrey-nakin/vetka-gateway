package com.vetka.gateway.mgmt.graphqlendpoint.resolver;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationInput;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationPayload;
import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpointCreationResponse;
import lombok.NonNull;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CreateGraphQlEndpointResolver {

    @MutationMapping
    public GraphQlEndpointCreationPayload createGraphQlEndpoint(@NonNull final GraphQlEndpointCreationInput input) {
        return GraphQlEndpointCreationResponse.builder().build();
    }
}
