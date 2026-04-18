package com.karandashov.jbcd.client.captcha.response;

public record CaptchaErrorResponse(
        Integer code,
        String message
) {
}
