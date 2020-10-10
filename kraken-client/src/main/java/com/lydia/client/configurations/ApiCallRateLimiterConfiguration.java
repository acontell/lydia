package com.lydia.client.configurations;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.bucket4j.Bandwidth.classic;
import static io.github.bucket4j.Bucket4j.builder;
import static io.github.bucket4j.Refill.greedy;
import static java.time.Duration.ofSeconds;

@Configuration
public class ApiCallRateLimiterConfiguration {

    private static final int NUMBER_OF_TOKENS_PER_REFILL = 1;

    @Bean
    public Bucket bucket(@Value("${kraken.api.call.rate.limit:15}") final long apiCallRateLimit,
                         @Value("${kraken.api.call.rate.refillInSeconds:3}") final long refillInSeconds) {

        return builder()
                .addLimit(classic(apiCallRateLimit, greedy(NUMBER_OF_TOKENS_PER_REFILL, ofSeconds(refillInSeconds))))
                .build();
    }
}
