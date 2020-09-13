package com.lydia.client.providers;

import com.lydia.client.model.request.Request;
import com.lydia.client.properties.KrakenApiProperties;
import com.lydia.client.resolvers.PostDataResolver;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.getInstance;
import static java.util.Base64.getEncoder;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class ApiRequestProvider {

    private static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap<>();

    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    private static final String API_KEY_HEADER_NAME = "API-Key";
    private static final String API_SIGN_HEADER_NAME = "API-Sign";
    private static final String NONCE_BODY_PARAM_NAME = "nonce";
    private static final int NONCE_TIME_MODIFIER = 10000;

    private final KrakenApiProperties properties;
    private final Mac mac;
    private final PostDataResolver postDataResolver;

    ApiRequestProvider(final KrakenApiProperties properties,
                       final Mac mac,
                       final PostDataResolver postDataResolver) {

        this.properties = properties;
        this.mac = mac;
        this.postDataResolver = postDataResolver;
    }

    public Request get(@NonNull final String endPoint) {

        return this.get(endPoint, EMPTY_MAP, EMPTY_MAP);
    }

    public Request getWithQueryParams(@NonNull final String endPoint,
                                      @NonNull final MultiValueMap<String, String> query) {

        return this.get(endPoint, query, EMPTY_MAP);
    }

    public Request getWithBody(@NonNull final String endPoint,
                               @NonNull final MultiValueMap<String, String> body) {

        return this.get(endPoint, EMPTY_MAP, body);
    }

    @SneakyThrows
    private Request get(@NonNull final String endPoint,
                        @NonNull final MultiValueMap<String, String> query,
                        @NonNull final MultiValueMap<String, String> body) {

        final var nonce = valueOf(currentTimeMillis() * NONCE_TIME_MODIFIER);
        final var signedMessage = this.getSignedMessage(endPoint, query, body, nonce);

        return new Request(this.getUri(endPoint, query), this.getHttEntity(nonce, body, signedMessage));
    }

    private String getSignedMessage(final String endPoint,
                                    final MultiValueMap<String, String> query,
                                    final MultiValueMap<String, String> body,
                                    final String nonce) throws NoSuchAlgorithmException {

        final var message = nonce + this.postDataResolver.resolve(query, body, nonce);
        final var hash = getInstance(MESSAGE_DIGEST_ALGORITHM).digest(message.getBytes(UTF_8));

        return getEncoder().encodeToString(this.mac.doFinal(addAll(endPoint.getBytes(), hash)));
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
