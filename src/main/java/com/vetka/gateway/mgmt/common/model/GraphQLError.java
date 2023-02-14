package com.vetka.gateway.mgmt.common.model;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class GraphQLError {

    @NonNull
    private final String message;
    @NonNull
    private final List<SourceLocation> locations;
}
