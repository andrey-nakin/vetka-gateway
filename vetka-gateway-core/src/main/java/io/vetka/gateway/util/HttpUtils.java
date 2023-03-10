package io.vetka.gateway.util;

import java.nio.charset.Charset;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class HttpUtils {

    public static Optional<Charset> contentTypeCharset(String type) {
        int i = type.indexOf(";");
        if (i >= 0) {
            type = type.substring(i + 1);
        }

        for (final var param : type.split("\\s*,\\s*")) {
            final var kv = param.split("\\s*=\\s*");
            if (kv.length > 1) {
                if ("charset".equalsIgnoreCase(kv[0].trim())) {
                    return Optional.of(Charset.forName(unquote(kv[1])));
                }
            }
        }

        return Optional.empty();
    }

    private static String unquote(final String s) {
        return StringUtils.strip(s, " \"");
    }
}
