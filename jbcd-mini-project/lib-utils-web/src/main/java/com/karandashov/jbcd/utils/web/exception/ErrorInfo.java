package com.karandashov.jbcd.utils.web.exception;

import org.springframework.http.HttpStatus;

public interface ErrorInfo {
    HttpStatus getHttpStatusCode();
    int getErrorCode();
    String getMessage();
}
