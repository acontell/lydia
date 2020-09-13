package com.lydia.client;

import com.lydia.client.providers.ApiRequestProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
public class KrakenApiClient {

    private final ApiRequestProvider apiRequestProvider;
    private final RestTemplate restTemplate;

    KrakenApiClient(final ApiRequestProvider apiRequestProvider,
                    final RestTemplate restTemplate) {

        this.apiRequestProvider = apiRequestProvider;
        this.restTemplate = restTemplate;
    }

    /* PUBLIC METHODS */

    public String getAssetsInfo() {

        final var request = this.apiRequestProvider.get("/0/public/Assets");

        return this.restTemplate.exchange(request.getUri(), GET, request.getEntity(), String.class).getBody();
    }

    /* PRIVATE METHODS */

    public String getAccountBalance() {

        final var request = this.apiRequestProvider.get("/0/private/Balance");

        return this.restTemplate.exchange(request.getUri(), POST, request.getEntity(), String.class).getBody();
    }

    public String getTradeBalance() {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add("asset", "ZEUR");
        final var request = this.apiRequestProvider.getWithBody("/0/private/TradeBalance", body);

        return this.restTemplate.exchange(request.getUri(), POST, request.getEntity(), String.class).getBody();
    }
}
