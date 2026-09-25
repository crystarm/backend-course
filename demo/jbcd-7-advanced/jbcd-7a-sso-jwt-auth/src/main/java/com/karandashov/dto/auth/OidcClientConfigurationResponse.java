package com.karandashov.dto.auth;

public record OidcClientConfigurationResponse(
        String issuerUri,
        String clientId,
        String redirectUri,
        String scope,
        int accessTokenExpiringNotificationSeconds
) {
}
