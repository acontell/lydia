package com.lydia.client.providers;

import com.lydia.client.properties.KrakenApiProperties;
import com.lydia.client.resolvers.NonceResolver;
import com.lydia.client.resolvers.SignedMessageResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.lydia.client.providers.ApiRequestProvider.API_KEY_HEADER_NAME;
import static com.lydia.client.providers.ApiRequestProvider.API_SIGN_HEADER_NAME;
import static com.lydia.client.providers.ApiRequestProvider.EMPTY_MAP;
import static com.lydia.client.providers.ApiRequestProvider.NONCE_BODY_PARAM_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@ExtendWith(MockitoExtension.class)
class ApiRequestProviderTest {

    private static final String URL = "http://api-fake.kraken.com";
    private static final String PRIVATE_KEY = "PRIVATE_KEY";
    private static final String PUBLIC_KEY = "PUBLIC_KEY";
    private static final KrakenApiProperties PROPERTIES = new KrakenApiProperties(URL, PRIVATE_KEY, PUBLIC_KEY);
    private static final String END_POINT = "/0/Private/Trade";
    private static final String NONCE = "NONCE";
    private static final String SIGNED_MESSAGE = "ENCODED_MESSAGE";
    private static final HttpHeaders HEADERS = new HttpHeaders();
    private static final MultiValueMap<String, String> BODY = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> BODY_WITH_EXTRA_PARAM = new LinkedMultiValueMap<>();
    private static final MultiValueMap<String, String> ONE_VALUE_MAP = new LinkedMultiValueMap<>();
    private static final String ONE_VALUE_MAP_KEY = "key";
    private static final String ONE_VALUE_MAP_VALUE = "value";

    static {

        HEADERS.add(API_KEY_HEADER_NAME, PUBLIC_KEY);
        HEADERS.add(API_SIGN_HEADER_NAME, SIGNED_MESSAGE);
        BODY.add(NONCE_BODY_PARAM_NAME, NONCE);
        BODY_WITH_EXTRA_PARAM.add(NONCE_BODY_PARAM_NAME, NONCE);
        BODY_WITH_EXTRA_PARAM.add(ONE_VALUE_MAP_KEY, ONE_VALUE_MAP_VALUE);
        ONE_VALUE_MAP.add(ONE_VALUE_MAP_KEY, ONE_VALUE_MAP_VALUE);
    }

    @Mock
    private SignedMessageResolver signedMessageResolver;
    @Mock
    private NonceResolver nonceResolver;

    private ApiRequestProvider provider;

    @BeforeEach
    void setUp() {

        given(this.nonceResolver.resolve()).willReturn(NONCE);

        this.provider = new ApiRequestProvider(PROPERTIES, this.signedMessageResolver, this.nonceResolver);
    }

    @Test
    void it_should_return_request_with_url() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.get(END_POINT);
        final var expected = fromHttpUrl(URL + END_POINT)
                .build()
                .toUri();

        assertThat(actual.getUri()).isEqualTo(expected);
    }

    @Test
    void it_should_return_request_with_url_and_query_params() {

        given(this.signedMessageResolver.resolve(END_POINT, ONE_VALUE_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithQueryParams(END_POINT, ONE_VALUE_MAP);
        final var expected = fromHttpUrl(URL + END_POINT)
                .queryParams(ONE_VALUE_MAP)
                .build()
                .toUri();

        assertThat(actual.getUri()).isEqualTo(expected);
    }

    @Test
    void it_should_return_request_with_url_and_no_query_params() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, ONE_VALUE_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithBody(END_POINT, ONE_VALUE_MAP);
        final var expected = fromHttpUrl(URL + END_POINT)
                .build()
                .toUri();

        assertThat(actual.getUri()).isEqualTo(expected);
    }

    @Test
    void it_should_return_security_headers_in_entity() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.get(END_POINT);

        assertThat(actual.getEntity().getHeaders()).isEqualTo(HEADERS);
    }

    @Test
    void it_should_return_security_headers_in_entity_get_with_params() {

        given(this.signedMessageResolver.resolve(END_POINT, ONE_VALUE_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithQueryParams(END_POINT, ONE_VALUE_MAP);

        assertThat(actual.getEntity().getHeaders()).isEqualTo(HEADERS);
    }

    @Test
    void it_should_return_security_headers_in_entity_get_with_body() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, ONE_VALUE_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithBody(END_POINT, ONE_VALUE_MAP);

        assertThat(actual.getEntity().getHeaders()).isEqualTo(HEADERS);
    }

    @Test
    void it_should_return_body_with_nonce() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.get(END_POINT);

        assertThat(actual.getEntity().getBody()).isEqualTo(BODY);
    }

    @Test
    void it_should_return_body_with_nonce_when_there_are_params_in_query() {

        given(this.signedMessageResolver.resolve(END_POINT, ONE_VALUE_MAP, EMPTY_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithQueryParams(END_POINT, ONE_VALUE_MAP);

        assertThat(actual.getEntity().getBody()).isEqualTo(BODY);
    }

    @Test
    void it_should_return_body_with_nonce_and_params_in_body() {

        given(this.signedMessageResolver.resolve(END_POINT, EMPTY_MAP, ONE_VALUE_MAP, NONCE)).willReturn(SIGNED_MESSAGE);

        final var actual = this.provider.getWithBody(END_POINT, ONE_VALUE_MAP);

        assertThat(actual.getEntity().getBody()).isEqualTo(BODY_WITH_EXTRA_PARAM);
    }
}