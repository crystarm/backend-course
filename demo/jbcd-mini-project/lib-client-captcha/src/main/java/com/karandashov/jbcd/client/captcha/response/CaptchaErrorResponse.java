package com.karandashov.jbcd.client.captcha.response;

public record CaptchaErrorResponse(
        String code,
        String message
) {
}
