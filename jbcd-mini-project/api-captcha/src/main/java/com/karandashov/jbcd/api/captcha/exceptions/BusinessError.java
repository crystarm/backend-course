package com.karandashov.jbcd.api.captcha.exceptions;

import com.karandashov.jbcd.utils.web.exception.ErrorInfo;
import org.springframework.http.HttpStatus;

public enum BusinessError implements ErrorInfo {

    CAPTCHA_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "Captcha not found or expired"),

    CAPTCHA_NOT_SOLVED(HttpStatus.BAD_REQUEST, 2001, "Captcha is not solved");

    private final HttpStatus httpStatusCode;

    private final int errorCode;

    private final String message;

    BusinessError(HttpStatus httpStatusCode, int errorCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return httpStatusCode;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
