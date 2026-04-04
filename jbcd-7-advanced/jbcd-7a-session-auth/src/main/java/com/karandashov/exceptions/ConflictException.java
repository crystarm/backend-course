package com.karandashov.exceptions;

public class ConflictException extends BaseException {

    public static final int HTTP_CODE = 409;

    public static final int CODE = 4002;

    public ConflictException(String message) {
        super(message);
    }

    @Override
    public int getHttpCode() {
        return HTTP_CODE;
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
