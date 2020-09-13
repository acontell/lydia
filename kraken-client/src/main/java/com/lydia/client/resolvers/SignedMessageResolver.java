package com.lydia.client.resolvers;

import lombok.SneakyThrows;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;
import java.security.MessageDigest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.MessageDigest.getInstance;
import static java.util.Base64.getEncoder;
import static org.apache.commons.lang3.ArrayUtils.addAll;

@Component
public class SignedMessageResolver {

    private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    private static final MessageDigest MESSAGE_DIGEST = getMessageDigest();
    private final Mac mac;
    private final PostDataResolver postDataResolver;

    SignedMessageResolver(final Mac mac,
                          final PostDataResolver postDataResolver) {

        this.mac = mac;
        this.postDataResolver = postDataResolver;
    }

    @SneakyThrows
    private static MessageDigest getMessageDigest() {

        return getInstance(MESSAGE_DIGEST_ALGORITHM);
    }

    public String resolve(@NonNull final String endPoint,
                          @NonNull final MultiValueMap<String, String> query,
                          @NonNull final MultiValueMap<String, String> body,
                          @NonNull final String nonce) {

        final var message = nonce + this.postDataResolver.resolve(query, body, nonce);
        final var hash = MESSAGE_DIGEST.digest(message.getBytes(UTF_8));

        return getEncoder().encodeToString(this.mac.doFinal(addAll(endPoint.getBytes(), hash)));
    }
}
