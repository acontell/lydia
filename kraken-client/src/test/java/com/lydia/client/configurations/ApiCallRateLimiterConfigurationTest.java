package com.lydia.client.configurations;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

class ApiCallRateLimiterConfigurationTest {


    public static final ApiCallRateLimiterConfiguration CONFIGURATION = new ApiCallRateLimiterConfiguration();

    @Test
    void it_should_create_bucket_with_given_number_of_tokens() {

        final var bucket = CONFIGURATION.bucket(7, 30);

        assertThat(bucket.tryConsume(10)).isFalse();
        assertThat(bucket.tryConsume(7)).isTrue();
    }

    @Test
    void it_should_create_bucket_that_refills_every_given_number_of_seconds() throws InterruptedException {

        final var bucket = CONFIGURATION.bucket(7, 1);

        bucket.tryConsume(1);
        sleep(1100);
        assertThat(bucket.tryConsume(7)).isTrue();
    }
}