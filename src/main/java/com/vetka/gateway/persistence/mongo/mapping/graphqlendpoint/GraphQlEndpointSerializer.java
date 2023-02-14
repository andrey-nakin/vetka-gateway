package com.vetka.gateway.persistence.mongo.mapping.graphqlendpoint;

import com.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import com.vetka.gateway.persistence.mongo.document.graphqlendpoint.GraphQlEndpointDocument;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.context.annotation.Lazy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Lazy
public abstract class GraphQlEndpointSerializer {

    public abstract GraphQlEndpoint toModel(GraphQlEndpointDocument src);

    @Mapping(target = "id", ignore = true)
    public abstract GraphQlEndpointDocument toDocument(Map<String, Object> src);

    @Mapping(target = "id", ignore = true)
    public abstract GraphQlEndpointDocument toDocument(@MappingTarget GraphQlEndpointDocument target,
            Map<String, Object> src);

    public static String mapString(Object value) {
        return value == null ? null : (String) value;
    }

    public static Integer mapInteger(Object value) {
        return value == null ? null : (Integer) value;
    }
}
