package io.vetka.gateway.persistence.inmemory.mapping;

import io.vetka.gateway.mgmt.graphqlendpoint.model.GraphQlEndpoint;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class InMemoryGraphQlEndpointMapper {

    public abstract GraphQlEndpoint toModel(GraphQlEndpoint src);

    @Mapping(target = "id", ignore = true)
    public abstract GraphQlEndpoint.GraphQlEndpointBuilder toEntity(
            @MappingTarget GraphQlEndpoint.GraphQlEndpointBuilder target, Map<String, Object> src);

    public static String mapString(Object value) {
        return value == null ? null : (String) value;
    }

    public static Integer mapInteger(Object value) {
        return value == null ? null : (Integer) value;
    }
}
