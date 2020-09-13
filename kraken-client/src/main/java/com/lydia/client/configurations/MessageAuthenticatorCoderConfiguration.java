package com.lydia.client.configurations;

import com.lydia.client.properties.KrakenApiProperties;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static java.util.Base64.getDecoder;
import static javax.crypto.Mac.getInstance;

@Configuration
public class MessageAuthenticatorCoderConfiguration {

    private static final String MESSAGE_AUTHENTICATOR_CODER_ALGORITHM = "HmacSHA512";

    @Bean
    @SneakyThrows
    public Mac mac(final KrakenApiProperties properties) {

        final var coder = getInstance(MESSAGE_AUTHENTICATOR_CODER_ALGORITHM);
        final var decodedApiKey = getDecoder().decode(properties.getPrivateKey());
        coder.init(new SecretKeySpec(decodedApiKey, MESSAGE_AUTHENTICATOR_CODER_ALGORITHM));

        return coder;
    }
}
