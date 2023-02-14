package com.vetka.gateway.mgmt.common.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SourceLocation {

    private final Integer line;
    private final Integer column;
}
