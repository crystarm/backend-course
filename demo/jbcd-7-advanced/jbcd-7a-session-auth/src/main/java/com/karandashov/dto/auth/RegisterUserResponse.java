package com.karandashov.dto.auth;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResponse(
        UUID id,
        String email,
        Instant createdAt
) {
}
