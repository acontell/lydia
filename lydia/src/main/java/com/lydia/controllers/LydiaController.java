package com.lydia.controllers;

import com.lydia.client.ApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class LydiaController {

    private final ApiClient client;

    public LydiaController(final ApiClient client) {

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
    public Object getTradesHistory() {

        return this.client.getTradesHistory(20);
    }
}
