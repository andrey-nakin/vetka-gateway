package io.vetka.gateway.schema.bo;

import graphql.schema.DataFetchingEnvironment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnvKey {

    @Getter
    private final DataFetchingEnvironment environment;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnvKey envKey = (EnvKey) o;
        return environment == envKey.environment;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
