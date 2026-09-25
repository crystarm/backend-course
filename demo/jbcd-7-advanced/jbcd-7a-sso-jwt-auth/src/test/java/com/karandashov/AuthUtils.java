package com.karandashov;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

public final class AuthUtils {

    private AuthUtils() {
    }

    public static RequestPostProcessor jwtUser() {
        return jwtUser(jwt -> {
        });
    }

    public static RequestPostProcessor jwtUser(Consumer<Jwt.Builder> customizer) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> {
                    jwt.tokenValue("test-id-token");
                    jwt.subject("dex-user-1");
                    jwt.claim("email", "student@example.com");
                    jwt.claim("name", "Student Example");
                    jwt.claim("preferred_username", "student");
                    jwt.claim("scope", "openid profile email offline_access");
                    jwt.claim("groups", List.of("students"));
                    jwt.issuedAt(Instant.now());
                    jwt.expiresAt(Instant.now().plusSeconds(60));
                    customizer.accept(jwt);
                });
    }
}
