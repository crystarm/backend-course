package com.karandashov.jbcd.api.captcha.exceptions;

import com.karandashov.jbcd.utils.web.exception.ErrorInfo;
import org.springframework.http.HttpStatus;

public enum BusinessError implements ErrorInfo {

    CAPTCHA_NOT_FOUND(HttpStatus.NOT_FOUND, "captcha.not.found.or.expired"),

    CAPTCHA_NOT_SOLVED(HttpStatus.BAD_REQUEST, "captcha.not.solved");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    BusinessError(HttpStatus httpStatusCode, String code) {
        this(httpStatusCode, code, null);
    }

    BusinessError(HttpStatus httpStatusCode, String code, String message) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
