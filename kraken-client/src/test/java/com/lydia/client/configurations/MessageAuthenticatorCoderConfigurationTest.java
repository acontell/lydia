package com.lydia.client.configurations;

import com.lydia.client.properties.KrakenApiProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageAuthenticatorCoderConfigurationTest {

    private static final KrakenApiProperties KRAKEN_API_PROPERTIES = new KrakenApiProperties("", "aa", "");
    private static final MessageAuthenticatorCoderConfiguration CONFIGURATION = new MessageAuthenticatorCoderConfiguration();

    @Test
    void it_should_create_message_authenticator_coder() {

        assertThat(CONFIGURATION.mac(KRAKEN_API_PROPERTIES)).isNotNull();
    }

    @Test
    void it_should_code_using_sha512_algorithm() {

        final var actual = CONFIGURATION.mac(KRAKEN_API_PROPERTIES).doFinal("a".getBytes());
        assertThat(actual).isEqualTo(new byte[]{-41, -82, 4, -109, 94, -105, 65, 30, -11, 124, -66, 74, -112, 87, -28, 28, -108, 6, -127, 73, -5, 121, -112, 41, 60, 124, 24, -112, -11, -44, -27, -4, -52, -118, -38, 126, 70, 108, -96, -13, -76, 48, 12, -54, 69, -13, 30, -24, 21, 8, 13, 88, -49, -45, -113, 121, 67, -121, -93, 46, -98, -47, -56, -51});
    }
}