package com.lydia.client;

import com.lydia.client.properties.KrakenPrivateEndPointProperties;
import com.lydia.client.properties.KrakenPublicEndPointProperties;
import com.lydia.client.providers.ApiRequestProvider;
import com.lydia.client.services.KrakenService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;

import static java.lang.String.join;
import static java.lang.String.valueOf;

@Component
public class ApiClient {

    static final String ASSET_POST_PARAM_KEY = "asset";
    static final String OFFSET_POST_PARAM_KEY = "ofs";
    static final String TYPE_POST_PARAM_KEY = "type";
    static final String PAIR_URI_PARAM_KEY = "pair";

    private final ApiRequestProvider apiRequestProvider;
    private final KrakenService krakenService;
    private final KrakenPrivateEndPointProperties privateEndPoints;
    private final KrakenPublicEndPointProperties publicEndPoints;

    ApiClient(final ApiRequestProvider apiRequestProvider,
              final KrakenService krakenService,
              final KrakenPrivateEndPointProperties privateEndPoints,
              final KrakenPublicEndPointProperties publicEndPoints) {

        this.apiRequestProvider = apiRequestProvider;
        this.krakenService = krakenService;
        this.privateEndPoints = privateEndPoints;
        this.publicEndPoints = publicEndPoints;
    }

    @Cacheable(cacheNames = "assetsInfo")
    public Object getAssetsInfo() {

        final var request = this.apiRequestProvider.get(this.publicEndPoints.getAssetsInfo());

        return this.krakenService.getAssetsInfo(request);
    }

    @Cacheable(cacheNames = "tickers")
    public Object getTickers(final List<String> tickers) {

        final var params = new LinkedMultiValueMap<String, String>();
        params.add(PAIR_URI_PARAM_KEY, join(",", tickers));

        final var request = this.apiRequestProvider.getWithQueryParams(this.publicEndPoints.getTickers(), params);

        return this.krakenService.getTickers(request);
    }

    @Cacheable(cacheNames = "accountBalance")
    public Object getAccountBalance() {

        final var request = this.apiRequestProvider.get(this.privateEndPoints.getAccountBalance());

        return this.krakenService.getAccountBalance(request);
    }

    @Cacheable(cacheNames = "tradeBalance")
    public Object getTradeBalance(final String asset) {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add(ASSET_POST_PARAM_KEY, asset);

        final var request = this.apiRequestProvider.getWithBody(this.privateEndPoints.getTradeBalance(), body);

        return this.krakenService.getTradeBalance(request);
    }

    @Cacheable(cacheNames = "tradesHistory")
    public Object getTradesHistory(final int offSet) {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add(OFFSET_POST_PARAM_KEY, valueOf(offSet));

        final var request = this.apiRequestProvider.getWithBody(this.privateEndPoints.getTradesHistory(), body);

        return this.krakenService.getTradesHistory(request);
    }

    @Cacheable(cacheNames = "ledgers")
    public Object getLedgers(final String type,
                             final String asset,
                             final int offSet) {

        final var body = new LinkedMultiValueMap<String, String>();
        body.add(TYPE_POST_PARAM_KEY, type);
        body.add(ASSET_POST_PARAM_KEY, asset);
        body.add(OFFSET_POST_PARAM_KEY, valueOf(offSet));

        final var request = this.apiRequestProvider.getWithBody(this.privateEndPoints.getLedgers(), body);

        return this.krakenService.getLedgers(request);
    }
}
