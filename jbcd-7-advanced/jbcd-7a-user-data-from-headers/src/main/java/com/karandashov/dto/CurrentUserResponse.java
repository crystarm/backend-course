package com.karandashov.dto;

import java.util.Map;

public record CurrentUserResponse(
        boolean authenticated,
        Map<String, Object> userInfo
) {
}
