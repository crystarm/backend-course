package com.karandashov.jbcd.utils.web.response;

public record ErrorResponse(
        Integer code,
        String message
) {
}
