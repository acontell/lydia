package com.lydia.client;

import com.lydia.client.properties.KrakenPrivateEndPointProperties;
import com.lydia.client.properties.KrakenPublicEndPointProperties;
import com.lydia.client.providers.ApiRequestProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Component
@CacheConfig(cacheNames = {"apiClient"})
public class ApiClient {

    static final String ASSET_POST_PARAM_KEY = "asset";

    private final ApiRequestProvider apiRequestProvider;
    private final RestTemplate restTemplate;
    private final KrakenPrivateEndPointProperties privateEndPoints;
    private final KrakenPublicEndPointProperties publicEndPoints;

    ApiClient(final ApiRequestProvider apiRequestProvider,
              final RestTemplate restTemplate,
              final KrakenPrivateEndPointProperties privateEndPoints,
              final KrakenPublicEndPointProperties publicEndPoints) {

        this.apiRequestProvider = apiRequestProvider;
        this.restTemplate = restTemplate;
        this.privateEndPoints = privateEndPoints;
        this.publicEndPoints = publicEndPoints;
    }

    @Cacheable
    public Object getAssetsInfo() {

        final var request = this.apiRequestProvider.get(this.publicEndPoints.getAssetsInfo());

        return this.restTemplate.exchange(request.getUri(), GET, request.getEntity(), Object.class).getBody();
    }

    @Cacheable
    public Object getAccountBalance() {

        final var request = this.apiRequestProvider.get(this.privateEndPoints.getAccountBalance());

        return this.restTemplate.exchange(request.getUri(), POST, request.getEntity(), Object.class).getBody();
    }

    @Cacheable
    public Object getTradeBalance(final String asset) {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add(ASSET_POST_PARAM_KEY, asset);

        final var request = this.apiRequestProvider.getWithBody(this.privateEndPoints.getTradeBalance(), body);

        return this.restTemplate.exchange(request.getUri(), POST, request.getEntity(), Object.class).getBody();
    }
}
