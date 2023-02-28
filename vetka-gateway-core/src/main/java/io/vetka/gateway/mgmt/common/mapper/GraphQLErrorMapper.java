package io.vetka.gateway.mgmt.common.mapper;

import io.vetka.gateway.mgmt.common.model.GraphQLError;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class GraphQLErrorMapper {

    @Mapping(target = "message")
    public abstract GraphQLError toModel(graphql.GraphQLError src);

    public List<GraphQLError> toModels(final List<graphql.GraphQLError> src) {
        return src.stream().map(this::toModel).toList();
    }
}
