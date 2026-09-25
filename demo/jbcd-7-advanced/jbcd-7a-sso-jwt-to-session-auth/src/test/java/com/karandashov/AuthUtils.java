package com.karandashov;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

public final class AuthUtils {

    private AuthUtils() {
    }

    public static RequestPostProcessor oidcUser() {
        return SecurityMockMvcRequestPostProcessors.oidcLogin()
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .idToken(token -> {
                    token.claim("sub", "dex-user-1");
                    token.claim("email", "student@example.com");
                    token.claim("name", "Student Example");
                    token.claim("preferred_username", "student");
                })
                .userInfoToken(userInfo -> {
                    userInfo.claim("email", "student@example.com");
                    userInfo.claim("name", "Student Example");
                    userInfo.claim("preferred_username", "student");
                    userInfo.claim("groups", List.of("students"));
                });
    }
}
