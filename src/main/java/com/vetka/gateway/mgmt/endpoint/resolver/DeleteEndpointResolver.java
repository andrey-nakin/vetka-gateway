package com.vetka.gateway.mgmt.endpoint.resolver;

import com.vetka.gateway.mgmt.endpoint.model.EndpointDeletionErrors;
import com.vetka.gateway.mgmt.endpoint.model.EndpointDeletionPayload;
import com.vetka.gateway.mgmt.endpoint.model.EndpointErrorUnknownId;
import java.util.List;
import lombok.NonNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DeleteEndpointResolver {

    @MutationMapping
    public EndpointDeletionPayload deleteEndpoint(@NonNull @Argument final String id) {
        return EndpointDeletionErrors.builder()
                .errors(List.of(EndpointErrorUnknownId.builder()
                        .message("There is no endpoint with the given ID")
                        .id(id)
                        .build()))
                .build();
    }
}
