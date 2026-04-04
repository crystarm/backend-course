package com.karandashov.dto.auth;

import java.util.Map;

public record AuthStateResponse(
        boolean authenticated,
        Map<String, Object> claims
) {
}
