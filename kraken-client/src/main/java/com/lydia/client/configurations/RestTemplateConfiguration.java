package com.lydia.client.configurations;

import com.lydia.client.properties.RestTemplateProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import static java.time.Duration.ofMillis;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder,
                                     final RestTemplateProperties properties) {

        return builder
                .setConnectTimeout(ofMillis(properties.getConnectTimeout()))
                .setReadTimeout(ofMillis(properties.getReadTimeout()))
                .build();
    }
}
