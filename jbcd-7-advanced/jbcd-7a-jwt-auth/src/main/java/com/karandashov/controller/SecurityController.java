package com.karandashov.controller;

import com.karandashov.dto.UniversalResponse;
import com.karandashov.dto.auth.AuthStateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SecurityController {

    @GetMapping("/me")
    public ResponseEntity<UniversalResponse<AuthStateResponse>> currentUser(
            Authentication authentication
    ) {
        boolean authenticated = authentication != null && authentication.isAuthenticated();

        AuthStateResponse response = new AuthStateResponse(authenticated, authenticated ? authentication.getName() : null);
        HttpStatus status = authenticated ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new UniversalResponse<>(response));
    }
}
