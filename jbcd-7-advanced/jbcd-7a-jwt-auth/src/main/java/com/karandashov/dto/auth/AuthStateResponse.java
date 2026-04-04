package com.karandashov.dto.auth;

public record AuthStateResponse(
        boolean authenticated,
        String email
) {
}
