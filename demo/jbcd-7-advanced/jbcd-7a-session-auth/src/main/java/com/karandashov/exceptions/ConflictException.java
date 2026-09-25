package com.karandashov.exceptions;

public class ConflictException extends BaseException {

    public static final int HTTP_CODE = 409;

    public static final String CODE = "conflict";

    public ConflictException(String message) {
        super(message);
    }

    @Override
    public int getHttpCode() {
        return HTTP_CODE;
    }

    @Override
    public String getCode() {
        return CODE;
    }
}
