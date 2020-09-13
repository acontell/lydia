package com.lydia.client.resolvers;

import org.springframework.stereotype.Component;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;

@Component
public class NonceResolver {

    private static final int NONCE_TIME_MODIFIER = 10000;

    public String resolve() {

        return valueOf(currentTimeMillis() * NONCE_TIME_MODIFIER);
    }
}
