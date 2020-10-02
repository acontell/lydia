package com.lydia.client.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "kraken.endpoint.private")
@Value
@ConstructorBinding
public class KrakenPrivateEndPointProperties {

    String accountBalance;
    String tradeBalance;
    String tradesHistory;
}
