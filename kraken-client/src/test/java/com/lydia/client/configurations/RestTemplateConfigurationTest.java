package com.lydia.client.configurations;

import com.lydia.client.properties.RestTemplateProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.Duration;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RestTemplateConfigurationTest {

    private static final int CONNECT_TIMEOUT = 1;
    private static final int READ_TIMEOUT = 2;

    private static final RestTemplateConfiguration REST_TEMPLATE_CONFIGURATION = new RestTemplateConfiguration();

    @Test
    void it_should_create_rest_template() {

        assertThat(REST_TEMPLATE_CONFIGURATION.restTemplate(new RestTemplateBuilder(), new RestTemplateProperties(CONNECT_TIMEOUT, READ_TIMEOUT))).isNotNull();
    }

    @Test
    void it_should_be_created_with_properties_values() {

        final var builderMock = mock(RestTemplateBuilder.class);
        given(builderMock.setConnectTimeout(any())).willReturn(builderMock);
        given(builderMock.setReadTimeout(any())).willReturn(builderMock);

        REST_TEMPLATE_CONFIGURATION.restTemplate(builderMock, new RestTemplateProperties(CONNECT_TIMEOUT, READ_TIMEOUT));

        verify(builderMock).setConnectTimeout(ofMillis(CONNECT_TIMEOUT));
        verify(builderMock).setReadTimeout(ofMillis(READ_TIMEOUT));
    }
}