package com.karandashov.controller;

import com.karandashov.AuthUtils;
import com.karandashov.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class SecurityTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void configShouldReturnOidcSettings() throws Exception {
        mockMvc.perform(get("/api/auth/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issuerUri").value("http://localhost:5556/dex"))
                .andExpect(jsonPath("$.clientId").value("jbcd-jwt-frontend"))
                .andExpect(jsonPath("$.redirectUri").value("http://localhost:8080/login.html"));
    }

    @Test
    void meShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.claims", aMapWithSize(0)));
    }

    @Test
    void meShouldReturnJwtClaimsForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .with(AuthUtils.jwtUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.claims.email").value("student@example.com"))
                .andExpect(jsonPath("$.claims.preferred_username").value("student"));
    }

    @Test
    void protectedEndpointShouldRejectRequestWithoutJwt() throws Exception {
        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Title",
                                  "message": "Message"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsPreflightShouldReturnConfiguredHeaders() throws Exception {
        mockMvc.perform(options("/api/news")
                        .header("Origin", "http://localhost:8080")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type,authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
}
