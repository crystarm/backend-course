package com.karandashov.jbcd.utils.web.response;

public record ErrorResponse(
        String code,
        String message
) {
}
