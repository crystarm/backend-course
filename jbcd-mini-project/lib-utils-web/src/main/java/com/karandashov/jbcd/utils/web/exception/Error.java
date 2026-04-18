package com.karandashov.jbcd.utils.web.exception;

import org.springframework.http.HttpStatus;

public enum Error implements ErrorInfo {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, 4000, "Bad request"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "Unexpected internal server error");

    private final HttpStatus httpStatusCode;

    private final int errorCode;

    private final String message;

    Error(HttpStatus httpStatusCode, int errorCode, String message) {
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
