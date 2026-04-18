package com.karandashov.jbcd.utils.web.exception;

public class BaseException extends RuntimeException {

    private final ErrorInfo error;

    public BaseException(ErrorInfo error) {
        super(error.getMessage());
        this.error = error;
    }

    public BaseException(ErrorInfo error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    public BaseException(ErrorInfo error, String message) {
        super(message);
        this.error = error;
    }

    public BaseException(ErrorInfo error, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
    }

    public ErrorInfo getError() {
        return error;
    }
}
