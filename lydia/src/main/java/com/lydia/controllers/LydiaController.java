package com.lydia.controllers;

import com.lydia.client.KrakenApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class LydiaController {

    private final KrakenApiClient client;

    public LydiaController(final KrakenApiClient client) {

        this.client = client;
    }

    @GetMapping(value = "/assets-info")
    public String getAssetsInfo() {

        return this.client.getAssetsInfo();
    }

    @GetMapping(value = "/account-balance")
    public String getAccountBalance() {

        return this.client.getAccountBalance();
    }

    @GetMapping(value = "/trade-balance")
    public String getTradeBalance() {

        return this.client.getTradeBalance("ZEUR");
    }
}
