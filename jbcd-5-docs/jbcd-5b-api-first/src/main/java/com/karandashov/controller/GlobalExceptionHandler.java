package com.karandashov.controller;

import com.karandashov.exceptions.BaseException;
import com.karandashov.news.dto.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<UniversalResponse> handleBaseException(BaseException ex) {
        log.error("Handling BaseException: {}", ex.getMessage());
        UniversalResponse response = new UniversalResponse().code(ex.getCode()).message(ex.getMessage());
        return ResponseEntity.status(ex.getHttpCode()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UniversalResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error("Validation error: {}", message);
        UniversalResponse response = new UniversalResponse().code(4001).message(message);
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<UniversalResponse> handleGeneralException(Exception ex) {
        log.error("Handling general exception", ex);
        UniversalResponse response = new UniversalResponse().code(5000).message("Internal Server Error: " + ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
