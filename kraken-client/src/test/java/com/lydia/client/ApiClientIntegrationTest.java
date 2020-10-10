package com.lydia.client;

import com.lydia.client.exceptions.ApiCallRateLimitExceededException;
import com.lydia.client.resolvers.NonceResolver;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

@SpringBootTest
public class ApiClientIntegrationTest {

    private static final URI HISTORY_URI = getUri();
    private static final String NONCE = "16023142482640000";
    private static final String API_KEY = "cTnTY40byVyXszYemDjAm8g7OIvq0mMoqPGlwYB0eD5MZKMcShv95SEy";
    private static final String API_SIGN = "b38u10aNHWH3e3TaQCplYS5S8xLaio3JLRsyq58Eqgqa88dd5hMRxT+IDI2B6SUulyGdRi5h1RLfeBQAVKdR9A==";
    private static final HttpEntity<MultiValueMap<String, String>> ENTITY = getHttEntity();
    private static final Object EXPECTED = "OK";
    private static final ResponseEntity<Object> RESPONSE = ResponseEntity.of(of(EXPECTED));
    @MockBean
    private NonceResolver nonceResolver;
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private ApiClient apiClient;

    @SneakyThrows
    private static URI getUri() {

        return new URI("https://api.kraken.com/0/private/TradesHistory");
    }

    private static HttpEntity<MultiValueMap<String, String>> getHttEntity() {

        final var headers = new HttpHeaders();
        headers.add("API-Key", API_KEY);
        headers.add("API-Sign", API_SIGN);

        final var bodyWithNonce = new LinkedMultiValueMap<String, String>();
        bodyWithNonce.add("nonce", NONCE);
        bodyWithNonce.add("ofs", "50");

        return new HttpEntity<>(bodyWithNonce, headers);
    }

    @BeforeEach
    void setUp() {

        given(this.nonceResolver.resolve()).willReturn(NONCE);
    }

    @Test
    void it_should_add_authentication_headers_to_request() {

        given(this.restTemplate.exchange(HISTORY_URI, POST, ENTITY, Object.class)).willReturn(RESPONSE);

        assertThat(this.apiClient.getTradesHistory(50)).isEqualTo(EXPECTED);
    }

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void it_should_cache_calls() {

        given(this.restTemplate.exchange(HISTORY_URI, POST, ENTITY, Object.class)).willReturn(RESPONSE);

        this.apiClient.getTradesHistory(50);
        this.apiClient.getTradesHistory(50);

        verify(this.restTemplate, times(1)).exchange(HISTORY_URI, POST, ENTITY, Object.class);
    }

    @DirtiesContext(methodMode = BEFORE_METHOD)
    @Test
    void it_should_impose_a_rate_limit() {

        given(this.restTemplate.exchange(any(), any(), any(), eq(Object.class))).willReturn(RESPONSE);

        this.apiClient.getTradeBalance("asset");
        this.apiClient.getTradesHistory(50);
        this.apiClient.getAccountBalance();

        assertThatExceptionOfType(ApiCallRateLimitExceededException.class).isThrownBy(() -> this.apiClient.getAssetsInfo());
    }
}

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
class TestApp {

}