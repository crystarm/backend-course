package com.karandashov.controller;

import com.karandashov.configuration.properties.OidcProperties;
import com.karandashov.dto.auth.AuthStateResponse;
import com.karandashov.dto.auth.OidcClientConfigurationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SecurityController {

    private final OidcProperties oidcProperties;

    @GetMapping("/config")
    public OidcClientConfigurationResponse config() {
        return new OidcClientConfigurationResponse(
                oidcProperties.getIssuerUri(),
                oidcProperties.getClientId(),
                oidcProperties.getFrontendBaseUrl() + "/login.html",
                oidcProperties.getScope(),
                oidcProperties.getAccessTokenExpiringNotificationSeconds()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<AuthStateResponse> currentUser(
            Authentication authentication
    ) {
        boolean authenticated = authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt;

        AuthStateResponse response = authenticated
                ? new AuthStateResponse(true, ((Jwt) authentication.getPrincipal()).getClaims())
                : new AuthStateResponse(false, Collections.emptyMap());
        HttpStatus status = authenticated ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(response);
    }
}
