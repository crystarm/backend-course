package com.karandashov.jbcd.utils.web.exception;

import org.springframework.http.HttpStatus;

public enum Error implements ErrorInfo {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "bad.request"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "internal.server.error");

    private final HttpStatus httpStatusCode;

    private final String code;

    private final String message;

    Error(HttpStatus httpStatusCode, String code) {
        this(httpStatusCode, code, null);
    }

    Error(HttpStatus httpStatusCode, String code, String message) {
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
