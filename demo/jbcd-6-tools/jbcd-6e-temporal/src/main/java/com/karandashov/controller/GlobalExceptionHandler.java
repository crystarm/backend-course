package com.karandashov.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus
    public Map<String, String> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return Map.of(
                "status", String.valueOf(status.value()),
                "error", status.getReasonPhrase(),
                "message", ex.getReason() == null ? status.getReasonPhrase() : ex.getReason()
        );
    }
}
