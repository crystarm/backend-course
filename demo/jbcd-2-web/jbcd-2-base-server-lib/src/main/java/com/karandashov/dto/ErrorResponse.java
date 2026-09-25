package com.karandashov.dto;

public record ErrorResponse(
        String code,
        String message
) {
}
