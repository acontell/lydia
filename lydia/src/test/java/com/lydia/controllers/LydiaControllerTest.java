package com.lydia.controllers;

import com.lydia.client.ApiClient;
import com.lydia.client.exceptions.ApiCallRateLimitExceededException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.lydia.controllers.LydiaController.EUR;
import static com.lydia.controllers.LydiaController.EUR_ASSET;
import static com.lydia.controllers.LydiaController.LEDGER_TYPE_DEPOSIT;
import static com.lydia.controllers.LydiaController.LEDGER_TYPE_WITHDRAWAL;
import static com.lydia.controllers.LydiaController.NO_OFFSET_VALUE;
import static com.lydia.controllers.LydiaController.RETRY_HEADER;
import static java.lang.Integer.parseInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LydiaControllerTest {

    private static final Object RESPONSE = "RESPONSE";
    private static final String API_PATH = "/api";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiClient apiClient;

    @Test
    void it_should_return_assets_info() {

        given(this.apiClient.getAssetsInfo()).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/assets-info"));
    }

    @SneakyThrows
    private void assertRequestIsCorrect(final MockHttpServletRequestBuilder requestBuilder) {

        this.mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().string("RESPONSE"));
    }

    @Test
    void it_should_return_tickers() {

        given(this.apiClient.getTickers(List.of("a" + EUR, "b" + EUR))).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/tickers").param("tickers", "a", "b"));
    }

    @Test
    void it_should_return_account_balance() {

        given(this.apiClient.getAccountBalance()).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/account-balance"));
    }

    @Test
    void it_should_return_trade_balance() {

        given(this.apiClient.getTradeBalance("ZEUR")).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/trade-balance"));
    }

    @Test
    void it_should_return_trades_history_with_default_offset_when_not_present() {

        given(this.apiClient.getTradesHistory(parseInt(NO_OFFSET_VALUE))).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/trades-history"));
    }

    @Test
    void it_should_return_trades_history_with_offset() {

        given(this.apiClient.getTradesHistory(400)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/trades-history").param("offset", "400"));
    }

    @Test
    void it_should_return_deposits() {

        given(this.apiClient.getLedgers(LEDGER_TYPE_DEPOSIT, EUR_ASSET, 400)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/get-deposits").param("offset", "400"));
    }

    @Test
    void it_should_return_withdraws() {

        given(this.apiClient.getLedgers(LEDGER_TYPE_WITHDRAWAL, EUR_ASSET, 400)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/get-withdraws").param("offset", "400"));
    }

    @Test
    void it_should_return_too_many_requests() throws Exception {

        given(this.apiClient.getTradesHistory(parseInt(NO_OFFSET_VALUE))).willThrow(new ApiCallRateLimitExceededException(100));

        this.mockMvc.perform(get(API_PATH + "/trades-history"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(RETRY_HEADER, "100"));
    }
}
