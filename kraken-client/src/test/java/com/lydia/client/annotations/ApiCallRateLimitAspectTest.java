package com.lydia.client.annotations;

import com.lydia.client.exceptions.ApiCallRateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ApiCallRateLimitAspectTest {

    private static final Object EXPECTED = "EXPECTED";

    @Mock
    private ApiCallRateLimit apiCallRateLimit;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private ConsumptionProbe probe;
    @Mock
    private Bucket bucket;
    @InjectMocks
    private ApiCallRateLimitAspect aspect;

    @Test
    void it_should_throw_exception_when_bucket_is_exhausted() {

        given(this.apiCallRateLimit.apiCallRateCost()).willReturn(1L);
        given(this.bucket.tryConsumeAndReturnRemaining(1L)).willReturn(this.probe);
        given(this.probe.isConsumed()).willReturn(false);
        given(this.probe.getNanosToWaitForRefill()).willReturn(1000000000L);

        assertThatExceptionOfType(ApiCallRateLimitExceededException.class)
                .isThrownBy(() -> this.aspect.controlApiCallRateLimit(this.joinPoint, this.apiCallRateLimit))
                .matches(e -> e.getTimeToWaitInSeconds() == 1L);
    }

    @Test
    void it_should_proceed_with_method_execution_when_bucket_is_not_exhausted() throws Throwable {

        given(this.apiCallRateLimit.apiCallRateCost()).willReturn(1L);
        given(this.bucket.tryConsumeAndReturnRemaining(1L)).willReturn(this.probe);
        given(this.probe.isConsumed()).willReturn(true);
        given(this.joinPoint.proceed()).willReturn(EXPECTED);

        final var actual = this.aspect.controlApiCallRateLimit(this.joinPoint, this.apiCallRateLimit);

        assertThat(actual).isEqualTo(EXPECTED);
    }
}