package com.lydia.client.resolvers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NonceResolverTest {

    private static final NonceResolver NONCE_RESOLVER = new NonceResolver();

    @Test
    void it_should_generate_nonce() {

        assertThat(NONCE_RESOLVER.resolve()).isNotNull();
    }

    @Test
    void it_should_increase_nonce_on_every_call() throws InterruptedException {

        final var actual1 = NONCE_RESOLVER.resolve();
        Thread.sleep(10);
        final var actual2 = NONCE_RESOLVER.resolve();

        assertThat(actual1).isLessThan(actual2);
    }

    @Test
    void it_should_be_longer_than_sixteen_characters() {

        final var actual = NONCE_RESOLVER.resolve();

        assertThat(actual.length()).isGreaterThan(16);
    }
}