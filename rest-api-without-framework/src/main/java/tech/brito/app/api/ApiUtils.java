package tech.brito.app.api;

import com.sun.net.httpserver.HttpExchange;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ApiUtils {

    public static Predicate<BigDecimal> bigDecimalPositive(){
        return n -> nonNull(n) && n.compareTo(BigDecimal.ZERO) > 0;
    }

    public static Predicate<HttpExchange> requestMethodIsPost(){
        return e -> "POST".equals(e.getRequestMethod());
    }

    public static Predicate<HttpExchange> requestMethodIsGet(){
        return e -> "GET".equals(e.getRequestMethod());
    }

    public static Map<String, List<String>> splitQuery(String query) {
        if (isNull( query) || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
            .map(s -> Arrays.copyOf(s.split("="), 2))
            .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
    }

    private static String decode(final String encoded) {
        try {
            return isNull(encoded) ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }
}
