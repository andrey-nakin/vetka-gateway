package io.vetka.gateway.transport.httpclient.service;

import java.util.List;
import java.util.Map;
import lombok.Setter;
import org.springframework.graphql.GraphQlResponse;
import org.springframework.graphql.ResponseError;
import org.springframework.graphql.ResponseField;

public class DefaultGraphQlResponse implements GraphQlResponse {

    @Setter
    private Map<String, Object> data;

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public <T> T getData() {
        return (T) data;
    }

    @Override
    public List<ResponseError> getErrors() {
        return null;
    }

    @Override
    public ResponseField field(String path) {
        return null;
    }

    @Override
    public Map<Object, Object> getExtensions() {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }
}
