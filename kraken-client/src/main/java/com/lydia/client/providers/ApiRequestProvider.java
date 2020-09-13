package com.lydia.client.providers;

import com.lydia.client.model.request.ApiRequest;
import com.lydia.client.properties.KrakenApiProperties;
import com.lydia.client.resolvers.NonceResolver;
import com.lydia.client.resolvers.SignedMessageResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class ApiRequestProvider {

    static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap<>();

    static final String API_KEY_HEADER_NAME = "API-Key";
    static final String API_SIGN_HEADER_NAME = "API-Sign";
    static final String NONCE_BODY_PARAM_NAME = "nonce";

    private final KrakenApiProperties properties;
    private final SignedMessageResolver signedMessageResolver;
    private final NonceResolver nonceResolver;

    ApiRequestProvider(final KrakenApiProperties properties,
                       final SignedMessageResolver signedMessageResolver,
                       final NonceResolver nonceResolver) {

        this.properties = properties;
        this.nonceResolver = nonceResolver;
        this.signedMessageResolver = signedMessageResolver;
    }

    public ApiRequest get(@NonNull final String endPoint) {

        return this.get(endPoint, EMPTY_MAP, EMPTY_MAP);
    }

    public ApiRequest getWithQueryParams(@NonNull final String endPoint,
                                         @NonNull final MultiValueMap<String, String> query) {

        return this.get(endPoint, query, EMPTY_MAP);
    }

    public ApiRequest getWithBody(@NonNull final String endPoint,
                                  @NonNull final MultiValueMap<String, String> body) {

        return this.get(endPoint, EMPTY_MAP, body);
    }

    private ApiRequest get(@NonNull final String endPoint,
                           @NonNull final MultiValueMap<String, String> query,
                           @NonNull final MultiValueMap<String, String> body) {

        final var nonce = this.nonceResolver.resolve();
        final var signedMessage = this.signedMessageResolver.resolve(endPoint, query, body, nonce);

        return new ApiRequest(this.getUri(endPoint, query), this.getHttEntity(nonce, body, signedMessage));
    }

    private URI getUri(final String endPoint, final MultiValueMap<String, String> query) {

        return fromHttpUrl(this.properties.getUrl() + endPoint)
                .queryParams(query)
                .build()
                .toUri();
    }

    private HttpEntity<MultiValueMap<String, String>> getHttEntity(final String nonce,
                                                                   final MultiValueMap<String, String> body,
                                                                   final String signedMessage) {

        final var headers = new HttpHeaders();
        headers.add(API_KEY_HEADER_NAME, this.properties.getPublicKey());
        headers.add(API_SIGN_HEADER_NAME, signedMessage);

        final var bodyWithNonce = new LinkedMultiValueMap<String, String>();
        bodyWithNonce.add(NONCE_BODY_PARAM_NAME, nonce);
        bodyWithNonce.addAll(body);

        return new HttpEntity<>(bodyWithNonce, headers);
    }
}
