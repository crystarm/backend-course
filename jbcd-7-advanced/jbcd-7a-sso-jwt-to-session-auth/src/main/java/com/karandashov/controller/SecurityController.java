package com.karandashov.controller;

import com.karandashov.dto.UniversalResponse;
import com.karandashov.dto.auth.AuthStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SecurityController {

    @GetMapping("/me")
    public ResponseEntity<UniversalResponse<AuthStateResponse>> currentUser(
            Authentication authentication,
            CsrfToken csrfToken
    ) {
        // Явно материализуем токен, чтобы Spring записал XSRF-TOKEN в cookie.
        csrfToken.getToken();

        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal;

        AuthStateResponse response = authenticated
                ? new AuthStateResponse(true, ((OAuth2AuthenticatedPrincipal) authentication.getPrincipal()).getAttributes())
                : new AuthStateResponse(false, Collections.emptyMap());
        HttpStatus status = authenticated ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new UniversalResponse<>(response));
    }
}
