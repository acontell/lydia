package com.lydia.client.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "kraken.endpoint.public")
@Value
@ConstructorBinding
public class KrakenPublicEndPointProperties {

    String assetsInfo;
}
