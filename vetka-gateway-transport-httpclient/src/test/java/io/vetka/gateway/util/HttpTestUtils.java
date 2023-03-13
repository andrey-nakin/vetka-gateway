package io.vetka.gateway.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

@UtilityClass
public class HttpTestUtils {

    public static HttpHeaders nonEmptyHttpHeaders() {
        final var map = new LinkedMultiValueMap<String, String>();
        map.add("test", "test");
        return HttpHeaders.readOnlyHttpHeaders(map);
    }
}
