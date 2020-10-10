package com.lydia.client.annotations;

import com.lydia.client.exceptions.ApiCallRateLimitExceededException;
import io.github.bucket4j.Bucket;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

@Aspect
@Component
public class ApiCallRateLimitAspect {

    private final Bucket bucket;

    ApiCallRateLimitAspect(final Bucket bucket) {

        this.bucket = bucket;
    }

    @Around("@annotation(apiCallRateLimit)")
    public Object controlApiCallRateLimit(final ProceedingJoinPoint joinPoint,
                                          final ApiCallRateLimit apiCallRateLimit) throws Throwable {

        final var probe = this.bucket.tryConsumeAndReturnRemaining(apiCallRateLimit.apiCallRateCost());

        if (!probe.isConsumed()) {

            throw new ApiCallRateLimitExceededException(NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
        }

        return joinPoint.proceed();
    }
}
