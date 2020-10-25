package com.lydia.client.services;

import com.lydia.client.annotations.ApiCallRateLimit;
import com.lydia.client.model.request.ApiRequest;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Service
public class KrakenService {

    private final RestTemplate restTemplate;

    KrakenService(final RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
    }

    @ApiCallRateLimit(apiCallRateCost = 1)
    public Object getAssetsInfo(final ApiRequest request) {

        return this.doExchange(request, GET);
    }

    private Object doExchange(final ApiRequest request, final HttpMethod method) {

        return this.restTemplate.exchange(request.getUri(), method, request.getEntity(), Object.class).getBody();
    }

    @ApiCallRateLimit(apiCallRateCost = 1)
    public Object getAccountBalance(final ApiRequest request) {

        return this.doExchange(request, POST);
    }

    @ApiCallRateLimit(apiCallRateCost = 2)
    public Object getTradeBalance(final ApiRequest request) {

        return this.doExchange(request, POST);
    }

    @ApiCallRateLimit(apiCallRateCost = 2)
    public Object getTradesHistory(final ApiRequest request) {

        return this.doExchange(request, POST);
    }

    @ApiCallRateLimit(apiCallRateCost = 2)
    public Object getLedgers(final ApiRequest request) {

        return this.doExchange(request, POST);
    }
}
