package com.lydia.client.resolvers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.crypto.Mac;

import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SignedMessageResolverTest {

    private static final String END_POINT = "/0/private/Trade";
    private static final MultiValueMap<String, String> EMPTY_MAP = new LinkedMultiValueMap<>();
    private static final String NONCE = "NONCE";
    private static final byte[] END_POINT_PLUS_HASH = addAll(END_POINT.getBytes(), new byte[]{-114, -22, 18, -78, -41, -60, -25, -100, -89, -8, -96, -117, 107, 51, 89, 44, -80, 64, 80, 86, 52, -116, 115, -101, 54, 81, 125, -67, 29, -36, -1, 10});

    @Mock
    private Mac mac;
    @Mock
    private PostDataResolver postDataResolver;

    @InjectMocks
    private SignedMessageResolver resolver;

    @Test
    void it_should_return_signed_message_using_nonce() {

        given(this.postDataResolver.resolve(EMPTY_MAP, EMPTY_MAP, NONCE)).willReturn("RESULT");
        given(this.mac.doFinal(END_POINT_PLUS_HASH)).willReturn(new byte[]{1, 2, 3});

        final var actual = this.resolver.resolve(END_POINT, EMPTY_MAP, EMPTY_MAP, NONCE);

        assertThat(actual).isEqualTo("AQID");
    }
}