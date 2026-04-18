package com.karandashov.jbcd.utils.web.handler;

import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.karandashov.jbcd.utils.web.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

import static com.karandashov.jbcd.utils.web.exception.Error.BAD_REQUEST;
import static com.karandashov.jbcd.utils.web.exception.Error.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation error: " + message, ex);
        ErrorResponse response = new ErrorResponse(BAD_REQUEST.getErrorCode(), "Validation error: " + message);
        return ResponseEntity.status(BAD_REQUEST.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        log.warn("Validation error: " + ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(BAD_REQUEST.getErrorCode(), "Validation error: " + ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST.getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        log.error("Handling base exception:", ex);
        ErrorResponse response = new ErrorResponse(ex.getError().getErrorCode(), ex.getError().getMessage());
        return ResponseEntity.status(ex.getError().getHttpStatusCode()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Handling unexpected exception: ", ex);
        ErrorResponse response = new ErrorResponse(INTERNAL_SERVER_ERROR.getErrorCode(), INTERNAL_SERVER_ERROR.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR.getHttpStatusCode()).body(response);
    }
}