package com.lydia.client.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "kraken.api")
@Value
@ConstructorBinding
public class KrakenApiProperties {

    String url;
    String privateKey;
    String publicKey;
}


