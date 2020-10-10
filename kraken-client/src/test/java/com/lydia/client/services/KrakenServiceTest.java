package com.lydia.client.services;

import com.lydia.client.model.request.ApiRequest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@ExtendWith(MockitoExtension.class)
class KrakenServiceTest {

    private static final URI SAMPLE_URI = getSampleUri();
    private static final HttpEntity<MultiValueMap<String, String>> SAMPLE_ENTITY = new HttpEntity<>(new LinkedMultiValueMap<>());
    private static final Object EXPECTED = "EXPECTED";
    private static final ApiRequest REQUEST = new ApiRequest(SAMPLE_URI, SAMPLE_ENTITY);
    @Mock
    private ResponseEntity<Object> responseEntity;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private KrakenService krakenService;

    @SneakyThrows
    private static URI getSampleUri() {

        return new URI("https://localhost:8080");
    }

    @BeforeEach
    void setUp() {

        given(this.responseEntity.getBody()).willReturn(EXPECTED);
    }

    @Test
    void it_send_request_for_get_assets_info() {

        given(this.restTemplate.exchange(SAMPLE_URI, GET, SAMPLE_ENTITY, Object.class)).willReturn(this.responseEntity);

        final var actual = this.krakenService.getAssetsInfo(REQUEST);

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_send_request_for_get_account_balance() {

        given(this.restTemplate.exchange(SAMPLE_URI, POST, SAMPLE_ENTITY, Object.class)).willReturn(this.responseEntity);

        final var actual = this.krakenService.getAccountBalance(REQUEST);

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_send_request_for_get_trade_balance() {

        given(this.restTemplate.exchange(SAMPLE_URI, POST, SAMPLE_ENTITY, Object.class)).willReturn(this.responseEntity);

        final var actual = this.krakenService.getTradeBalance(REQUEST);

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_send_request_for_get_trades_history() {

        given(this.restTemplate.exchange(SAMPLE_URI, POST, SAMPLE_ENTITY, Object.class)).willReturn(this.responseEntity);

        final var actual = this.krakenService.getTradesHistory(REQUEST);

        assertThat(actual).isEqualTo(EXPECTED);
    }
}