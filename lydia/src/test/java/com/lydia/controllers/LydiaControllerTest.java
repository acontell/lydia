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

import static com.lydia.controllers.LydiaController.BANK_METHOD;
import static com.lydia.controllers.LydiaController.DEFAULT_OFFSET_VALUE;
import static com.lydia.controllers.LydiaController.EUR_ASSET;
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

        given(this.apiClient.getTradesHistory(parseInt(DEFAULT_OFFSET_VALUE))).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/trades-history"));
    }

    @Test
    void it_should_return_trades_history_with_offset() {

        given(this.apiClient.getTradesHistory(400)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/trades-history").param("offset", "400"));
    }

    @Test
    void it_should_return_deposit_status() {

        given(this.apiClient.getDepositStatus(EUR_ASSET, BANK_METHOD)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/deposit-status"));
    }

    @Test
    void it_should_return_withdraw_status() {

        given(this.apiClient.getWithdrawStatus(EUR_ASSET, BANK_METHOD)).willReturn(RESPONSE);

        this.assertRequestIsCorrect(get(API_PATH + "/withdraw-status"));
    }

    @Test
    void it_should_return_too_many_requests() throws Exception {

        given(this.apiClient.getTradesHistory(parseInt(DEFAULT_OFFSET_VALUE))).willThrow(new ApiCallRateLimitExceededException(100));

        this.mockMvc.perform(get(API_PATH + "/trades-history"))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(RETRY_HEADER, "100"));
    }
}
