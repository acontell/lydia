package com.lydia.client.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiCallRateLimitExceededException extends RuntimeException {

    private static final long serialVersionUID = -2937823817386381878L;

    private final long timeToWaitInSeconds;
}
