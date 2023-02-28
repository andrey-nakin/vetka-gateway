package io.vetka.gateway.objectmap.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObjectMapperHelper {

    @Getter
    private final ObjectMapper objectMapper;

    public void copy(final Object src, final Consumer<InputStream> writer) throws IOException {
        try (var input = new PipedInputStream(); var output = new PipedOutputStream(input)) {
            writer.accept(input);
            objectMapper.writeValue(output, src);
        }
    }
}
