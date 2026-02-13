package com.karandashov.exceptions;

/**
 * Универсальное исключение, чтобы преобразовывать ошибки в {@link com.karandashov.dto.UniversalResponse}
 */
@SuppressWarnings("unused")
public abstract class BaseException extends RuntimeException {

    public static final int HTTP_CODE = 500;

    public static final int CODE = 5000;

    public BaseException(String message) {
        super(message);
    }

    public int getHttpCode() {
        return HTTP_CODE;
    }

    public int getCode() {
        return CODE;
    }
}
