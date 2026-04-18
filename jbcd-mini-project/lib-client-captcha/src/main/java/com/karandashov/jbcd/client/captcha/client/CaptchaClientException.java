package com.karandashov.jbcd.client.captcha.client;

import org.springframework.http.HttpStatusCode;

public class CaptchaClientException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final Integer code;

    public CaptchaClientException(HttpStatusCode statusCode, Integer code, String message) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Integer getCode() {
        return code;
    }
}
