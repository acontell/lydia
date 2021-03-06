package com.lydia.client;


import com.lydia.client.model.request.ApiRequest;
import com.lydia.client.properties.KrakenPrivateEndPointProperties;
import com.lydia.client.properties.KrakenPublicEndPointProperties;
import com.lydia.client.providers.ApiRequestProvider;
import com.lydia.client.services.KrakenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static com.lydia.client.ApiClient.ASSET_POST_PARAM_KEY;
import static com.lydia.client.ApiClient.OFFSET_POST_PARAM_KEY;
import static com.lydia.client.ApiClient.PAIR_URI_PARAM_KEY;
import static com.lydia.client.ApiClient.TYPE_POST_PARAM_KEY;
import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {

    private static final String END_POINT = "END_POINT";
    private static final String EXPECTED = "EXPECTED";

    @Mock
    private ApiRequest request;
    @Mock
    private ApiRequestProvider apiRequestProvider;
    @Mock
    private KrakenService krakenService;
    @Mock
    private KrakenPrivateEndPointProperties privateEndPoints;
    @Mock
    private KrakenPublicEndPointProperties publicEndPoints;

    @InjectMocks
    private ApiClient client;

    @Test
    void it_should_get_assets_info_using_made_request() {

        given(this.publicEndPoints.getAssetsInfo()).willReturn(END_POINT);
        given(this.apiRequestProvider.get(END_POINT)).willReturn(this.request);
        given(this.krakenService.getAssetsInfo(this.request)).willReturn(EXPECTED);

        final var actual = this.client.getAssetsInfo();

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_tickers_using_made_request() {

        final var params = new LinkedMultiValueMap<String, String>();
        params.add(PAIR_URI_PARAM_KEY, "a,b,c");

        given(this.publicEndPoints.getTickers()).willReturn(END_POINT);
        given(this.apiRequestProvider.getWithQueryParams(END_POINT, params)).willReturn(this.request);
        given(this.krakenService.getTickers(this.request)).willReturn(EXPECTED);

        final var actual = this.client.getTickers(List.of("a", "b", "c"));

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_account_balance_using_made_request() {

        given(this.privateEndPoints.getAccountBalance()).willReturn(END_POINT);
        given(this.apiRequestProvider.get(END_POINT)).willReturn(this.request);
        given(this.krakenService.getAccountBalance(this.request)).willReturn(EXPECTED);

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
        given(this.krakenService.getTradeBalance(this.request)).willReturn(EXPECTED);

        final var actual = this.client.getTradeBalance(asset);

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_trades_history_using_made_request() {

        final var offset = 1;
        final var body = new LinkedMultiValueMap<String, String>();
        body.add(OFFSET_POST_PARAM_KEY, valueOf(offset));

        given(this.privateEndPoints.getTradesHistory()).willReturn(END_POINT);
        given(this.apiRequestProvider.getWithBody(END_POINT, body)).willReturn(this.request);
        given(this.krakenService.getTradesHistory(this.request)).willReturn(EXPECTED);

        final var actual = this.client.getTradesHistory(offset);

        assertThat(actual).isEqualTo(EXPECTED);
    }

    @Test
    void it_should_get_ledgers_using_made_request() {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add(TYPE_POST_PARAM_KEY, "type");
        body.add(ASSET_POST_PARAM_KEY, "asset");
        body.add(OFFSET_POST_PARAM_KEY, "1");

        given(this.privateEndPoints.getLedgers()).willReturn(END_POINT);
        given(this.apiRequestProvider.getWithBody(END_POINT, body)).willReturn(this.request);
        given(this.krakenService.getLedgers(this.request)).willReturn(EXPECTED);

        final var actual = this.client.getLedgers("type", "asset", 1);

        assertThat(actual).isEqualTo(EXPECTED);
    }
}
