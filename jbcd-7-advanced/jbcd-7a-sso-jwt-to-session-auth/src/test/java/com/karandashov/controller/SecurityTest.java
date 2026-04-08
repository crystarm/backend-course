package com.karandashov.controller;

import com.karandashov.AuthUtils;
import com.karandashov.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SecurityTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void meShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.authenticated").value(false))
                .andExpect(jsonPath("$.data.userInfo", aMapWithSize(0)));
    }

    @Test
    void meShouldReturnOidcUserInfoForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .with(AuthUtils.oidcUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.authenticated").value(true))
                .andExpect(jsonPath("$.data.userInfo.email").value("student@example.com"))
                .andExpect(jsonPath("$.data.userInfo.preferred_username").value("student"))
                .andExpect(jsonPath("$.data.userInfo.groups[0]").value("students"));
    }

    @Test
    void loginEndpointShouldRedirectToDexAuthorization() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/dex"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/dex/auth")));
    }

    @Test
    void protectedEndpointShouldRedirectRequestWithoutSession() throws Exception {
        mockMvc.perform(post("/api/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Title",
                                  "message": "Message"
                                }
                                """))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void protectedEndpointShouldRejectRequestWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/api/news")
                        .with(AuthUtils.oidcUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Title",
                                  "message": "Message"
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void corsPreflightShouldReturnConfiguredHeaders() throws Exception {
        mockMvc.perform(options("/api/news")
                        .header("Origin", "http://localhost:8080")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type,x-xsrf-token"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }
}
