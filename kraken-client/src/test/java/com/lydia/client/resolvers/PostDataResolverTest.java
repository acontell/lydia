package com.lydia.client.resolvers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class PostDataResolverTest {

    private static final PostDataResolver RESOLVER = new PostDataResolver();
    private static final String NONCE = "NONCE";

    private MultiValueMap<String, String> query;
    private MultiValueMap<String, String> body;

    @BeforeEach
    void setUp() {

        this.query = new LinkedMultiValueMap<>();
        this.body = new LinkedMultiValueMap<>();
    }

    @Test
    void it_should_have_only_nonce_when_query_and_body_are_empty() {

        final var actual = RESOLVER.resolve(this.query, this.body, NONCE);

        assertThat(actual).isEqualTo("nonce=NONCE");
    }

    @Test
    void it_should_have_nonce_and_query_when_body_is_empty() {

        this.query.add("key", "query");

        final var actual = RESOLVER.resolve(this.query, this.body, NONCE);

        assertThat(actual).isEqualTo("nonce=NONCE&key=query");
    }

    @Test
    void it_should_have_nonce_and_body_when_query_is_empty() {

        this.body.add("key", "body");

        final var actual = RESOLVER.resolve(this.query, this.body, NONCE);

        assertThat(actual).isEqualTo("nonce=NONCE&key=body");
    }

    @Test
    void it_should_have_nonce_body_and_query_in_that_order() {

        this.query.add("key", "query");
        this.body.add("key", "body");

        final var actual = RESOLVER.resolve(this.query, this.body, NONCE);

        assertThat(actual).isEqualTo("nonce=NONCE&key=query&key=body");
    }
}