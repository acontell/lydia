package com.lydia.client.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "kraken.rest-template")
@Value
@ConstructorBinding
public class RestTemplateProperties {

    long connectTimeout;
    long readTimeout;
}
