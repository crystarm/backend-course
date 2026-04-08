package com.karandashov.controller;

import com.karandashov.configuration.properties.OidcProperties;
import com.karandashov.dto.UniversalResponse;
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

    @GetMapping("/me")
    public ResponseEntity<UniversalResponse<????>> currentUser(

    ) {
        // todo правильная реализация
    }
}
