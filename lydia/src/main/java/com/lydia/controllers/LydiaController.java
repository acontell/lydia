package com.lydia.controllers;

import com.lydia.client.ApiClient;
import com.lydia.client.exceptions.ApiCallRateLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RestController
@RequestMapping("/api")
public class LydiaController {

    static final String RETRY_HEADER = "X-Rate-Limit-Retry-After-Seconds";
    static final String DEFAULT_OFFSET_VALUE = "20";
    private final ApiClient client;

    LydiaController(final ApiClient client) {

        this.client = client;
    }

    @GetMapping(value = "/assets-info")
    public Object getAssetsInfo() {

        return this.client.getAssetsInfo();
    }

    @GetMapping(value = "/account-balance")
    public Object getAccountBalance() {

        return this.client.getAccountBalance();
    }

    @GetMapping(value = "/trade-balance")
    public Object getTradeBalance() {

        return this.client.getTradeBalance("ZEUR");
    }

    @GetMapping(value = "/trades-history")
    public Object getTradesHistory(@RequestParam(defaultValue = DEFAULT_OFFSET_VALUE) int offset) {

        return this.client.getTradesHistory(offset);
    }

    @ExceptionHandler(ApiCallRateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimitExceededException(final ApiCallRateLimitExceededException e) {

        return ResponseEntity
                .status(TOO_MANY_REQUESTS)
                .header(RETRY_HEADER, Long.toString(e.getTimeToWaitInSeconds()))
                .build();
    }
}
