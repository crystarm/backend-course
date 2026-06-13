package com.karandashov.exceptions;

/** * Универсальное исключение
 */
@SuppressWarnings("unused")
public abstract class BaseException extends RuntimeException {

    public static final int HTTP_CODE = 500;

    public static final String CODE = "internal.server.error";

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getHttpCode() {
        return HTTP_CODE;
    }

    public String getCode() {
        return CODE;
    }
}
