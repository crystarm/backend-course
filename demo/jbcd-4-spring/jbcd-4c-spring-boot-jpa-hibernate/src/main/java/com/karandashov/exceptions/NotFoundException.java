package com.karandashov.exceptions;

public class NotFoundException extends BaseException {

    public static final int HTTP_CODE = 404;

    public static final String CODE = "news.not.found";

    public NotFoundException(String message) {
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
