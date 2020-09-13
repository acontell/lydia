package com.lydia.client.resolvers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Component
public class PostDataResolver {

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String POST_PARAMS_LIST_DELIMITER = ",";

    public String resolve(final MultiValueMap<String, String> query,
                          final MultiValueMap<String, String> body,
                          final String nonce) {

        return this.joinQueryNonEmpty("nonce=" + nonce, this.toQueryString(query), this.toQueryString(body));
    }

    private String joinQueryNonEmpty(final String... strings) {

        return stream(strings)
                .filter(StringUtils::isNotEmpty)
                .collect(joining(QUERY_PARAM_DELIMITER));
    }

    private String toQueryString(final MultiValueMap<String, String> map) {

        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + join(POST_PARAMS_LIST_DELIMITER, entry.getValue()))
                .collect(joining(QUERY_PARAM_DELIMITER));
    }
}
