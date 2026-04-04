package com.karandashov.controller;

import com.karandashov.AuthUtils;
import com.karandashov.BaseTest;
import com.karandashov.dto.auth.RegisterUserRequest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class SecurityTest extends BaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registrationLoginMeLogoutShouldWorkWithRedisSession() throws Exception {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        httpClient.send(HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/me")).GET().build(), HttpResponse.BodyHandlers.ofString());
        String csrfToken = AuthUtils.getCookieValue(
                cookieManager,
                AuthUtils.uri(port, "/"),
                AuthUtils.CSRF_COOKIE_NAME
        );
        RegisterUserRequest request = new RegisterUserRequest("student@example.com", "password123");

        HttpResponse<String> registrationResponse = httpClient.send(
                HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/registration"))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header(AuthUtils.CSRF_HEADER_NAME, csrfToken)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(request)))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, registrationResponse.statusCode());
        assertEquals("student@example.com", jsonMapper.readTree(registrationResponse.body()).path("data").path("email").asString());

        HttpResponse<String> loginResponse = httpClient.send(
                HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/login"))
                        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header(AuthUtils.CSRF_HEADER_NAME, csrfToken)
                        .POST(HttpRequest.BodyPublishers.ofString("email=student%40example.com&password=password123"))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(302, loginResponse.statusCode());
        URI loginLocation = URI.create(loginResponse.headers().firstValue("location").orElseThrow());
        assertEquals("/", loginLocation.getPath());

        Set<String> keysAfterLogin = stringRedisTemplate.keys("jbcd:7a:session-auth:*");
        assertTrue(keysAfterLogin != null && !keysAfterLogin.isEmpty());

        HttpResponse<String> meResponse = httpClient.send(
                HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/me")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, meResponse.statusCode());
        assertEquals("student@example.com", jsonMapper.readTree(meResponse.body()).path("data").path("email").asString());

        httpClient.send(HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/me")).GET().build(), HttpResponse.BodyHandlers.ofString());
        String logoutCsrfToken = AuthUtils.getCookieValue(
                cookieManager,
                AuthUtils.uri(port, "/"),
                AuthUtils.CSRF_COOKIE_NAME
        );
        HttpResponse<String> logoutResponse = httpClient.send(
                HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/logout"))
                        .header(AuthUtils.CSRF_HEADER_NAME, logoutCsrfToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(302, logoutResponse.statusCode());
        URI logoutLocation = URI.create(logoutResponse.headers().firstValue("location").orElseThrow());
        assertEquals("/login", logoutLocation.getPath());
        assertEquals("logout", logoutLocation.getQuery());

        HttpResponse<String> anonymousMeResponse = httpClient.send(
                HttpRequest.newBuilder(AuthUtils.uri(port, "/api/auth/me")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(401, anonymousMeResponse.statusCode());
        assertFalse(jsonMapper.readTree(anonymousMeResponse.body()).path("data").path("authenticated").asBoolean());
    }

    @Test
    void registrationShouldRejectDuplicateEmail() throws Exception {
        Cookie csrfCookie = AuthUtils.fetchCsrfCookie(mockMvc);
        RegisterUserRequest request = new RegisterUserRequest("student@example.com", "password123");

        mockMvc.perform(post("/api/auth/registration")
                        .cookie(csrfCookie)
                        .header(AuthUtils.CSRF_HEADER_NAME, csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Cookie secondCsrfCookie = AuthUtils.fetchCsrfCookie(mockMvc);
        mockMvc.perform(post("/api/auth/registration")
                        .cookie(secondCsrfCookie)
                        .header(AuthUtils.CSRF_HEADER_NAME, secondCsrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(4002))
                .andExpect(jsonPath("$.message").value("Пользователь с таким email уже существует"));
    }

    @Test
    void meShouldReturnUnauthorizedForAnonymousUser() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.authenticated").value(false))
                .andExpect(jsonPath("$.data.email").value(nullValue()));
    }

    @Test
    void protectedEndpointShouldRejectRequestWithoutCsrfToken() throws Exception {
        AuthUtils.AuthenticatedClient authenticatedClient = AuthUtils.loginTestUser(mockMvc, userRepository, passwordEncoder);
        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie())
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
