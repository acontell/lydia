package com.lydia.client;


import com.lydia.client.model.request.Request;
import com.lydia.client.properties.KrakenPrivateEndPointProperties;
import com.lydia.client.properties.KrakenPublicEndPointProperties;
import com.lydia.client.providers.ApiRequestProvider;
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

import static com.lydia.client.KrakenApiClient.ASSET_POST_PARAM_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@ExtendWith(MockitoExtension.class)
class KrakenApiClientTest {

    private static final String END_POINT = "END_POINT";
    private static final HttpEntity<MultiValueMap<String, String>> ENTITY = new HttpEntity<>(new LinkedMultiValueMap<>());
    private static final String EXPECTED = "EXPECTED";

    @Mock
    private Request request;
    @Mock
    private URI uri;
    @Mock
    private ResponseEntity<String> responseEntity;
    @Mock
    private ApiRequestProvider apiRequestProvider;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private KrakenPrivateEndPointProperties privateEndPoints;
    @Mock
    private KrakenPublicEndPointProperties publicEndPoints;

    @InjectMocks
    private KrakenApiClient client;

    @Test
    void it_should_get_assets_info_using_made_request() {

        given(this.publicEndPoints.getAssetsInfo()).willReturn(END_POINT);
        given(this.apiRequestProvider.get(END_POINT)).willReturn(this.request);
        given(this.request.getUri()).willReturn(this.uri);
        given(this.request.getEntity()).willReturn(ENTITY);
        given(this.restTemplate.exchange(this.uri, GET, ENTITY, String.class)).willReturn(this.responseEntity);
        given(this.responseEntity.getBody()).willReturn(EXPECTED);

        final var actual = this.client.getAssetsInfo();

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_account_balance_using_made_request() {

        given(this.privateEndPoints.getAccountBalance()).willReturn(END_POINT);
        given(this.apiRequestProvider.get(END_POINT)).willReturn(this.request);
        given(this.request.getUri()).willReturn(this.uri);
        given(this.request.getEntity()).willReturn(ENTITY);
        given(this.restTemplate.exchange(this.uri, POST, ENTITY, String.class)).willReturn(this.responseEntity);
        given(this.responseEntity.getBody()).willReturn(EXPECTED);

        final var actual = this.client.getAccountBalance();

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_trade_balance_using_made_request() {

        final var asset = "asset";
        final var body = new LinkedMultiValueMap<String, String>();
        body.add(ASSET_POST_PARAM_KEY, asset);

        given(this.privateEndPoints.getTradeBalance()).willReturn(END_POINT);
        given(this.apiRequestProvider.getWithBody(END_POINT, body)).willReturn(this.request);
        given(this.request.getUri()).willReturn(this.uri);
        given(this.request.getEntity()).willReturn(ENTITY);
        given(this.restTemplate.exchange(this.uri, POST, ENTITY, String.class)).willReturn(this.responseEntity);
        given(this.responseEntity.getBody()).willReturn(EXPECTED);

        final var actual = this.client.getTradeBalance(asset);

        assertThat(actual).isEqualTo(EXPECTED);
    }
}