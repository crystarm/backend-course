package com.karandashov.jbcd.api.news.exceptions;

import com.karandashov.jbcd.utils.web.exception.ErrorInfo;
import org.springframework.http.HttpStatus;

public enum BusinessError implements ErrorInfo {

    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, 1000, "News not found"),
    CAPTCHA_NOT_FOUND_OR_EXPIRED(HttpStatus.BAD_REQUEST, 1001, "Captcha not found or expired"),
    CAPTCHA_NOT_SOLVED(HttpStatus.BAD_REQUEST, 1002, "Captcha is not solved"),
    CAPTCHA_CHECK_FAILED(HttpStatus.BAD_GATEWAY, 1003, "Failed to check captcha");

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
