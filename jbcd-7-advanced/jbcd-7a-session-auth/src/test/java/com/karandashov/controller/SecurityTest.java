package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.dto.auth.RegisterUserRequest;
import com.karandashov.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SecurityTest extends BaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void tearDown() {
        userRepository.deleteAll();
        deleteRedisKeys();
    }

    @Test
    void registrationLoginMeLogoutShouldWorkWithRedisSession() throws Exception {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        httpClient.send(HttpRequest.newBuilder(uri("/api/auth/me")).GET().build(), HttpResponse.BodyHandlers.ofString());
        String csrfToken = getCookieValue(cookieManager);
        RegisterUserRequest request = new RegisterUserRequest("student@example.com", "password123");

        HttpResponse<String> registrationResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/api/auth/registration"))
                        .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .header("X-XSRF-TOKEN", csrfToken)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(request)))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, registrationResponse.statusCode());
        assertEquals("student@example.com", jsonMapper.readTree(registrationResponse.body()).path("data").path("email").asString());

        HttpResponse<String> loginResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/api/auth/login"))
                        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .header("X-XSRF-TOKEN", csrfToken)
                        .POST(HttpRequest.BodyPublishers.ofString("email=student%40example.com&password=password123"))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, loginResponse.statusCode());
        assertTrue(jsonMapper.readTree(loginResponse.body()).path("data").path("authenticated").asBoolean());

        Set<String> keysAfterLogin = stringRedisTemplate.keys("jbcd:7a:session-auth:*");
        assertTrue(keysAfterLogin != null && !keysAfterLogin.isEmpty());

        HttpResponse<String> meResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/api/auth/me")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, meResponse.statusCode());
        assertEquals("student@example.com", jsonMapper.readTree(meResponse.body()).path("data").path("email").asString());

        httpClient.send(HttpRequest.newBuilder(uri("/api/auth/me")).GET().build(), HttpResponse.BodyHandlers.ofString());
        String logoutCsrfToken = getCookieValue(cookieManager);
        HttpResponse<String> logoutResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/api/auth/logout"))
                        .header("X-XSRF-TOKEN", logoutCsrfToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, logoutResponse.statusCode());
        assertFalse(jsonMapper.readTree(logoutResponse.body()).path("data").path("authenticated").asBoolean());

        HttpResponse<String> anonymousMeResponse = httpClient.send(
                HttpRequest.newBuilder(uri("/api/auth/me")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(401, anonymousMeResponse.statusCode());
        assertFalse(jsonMapper.readTree(anonymousMeResponse.body()).path("data").path("authenticated").asBoolean());
    }

    @Test
    void registrationShouldRejectDuplicateEmail() throws Exception {
        Cookie csrfCookie = fetchCsrfCookie();
        RegisterUserRequest request = new RegisterUserRequest("student@example.com", "password123");

        mockMvc.perform(post("/api/auth/registration")
                        .cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Cookie secondCsrfCookie = fetchCsrfCookie();
        mockMvc.perform(post("/api/auth/registration")
                        .cookie(secondCsrfCookie)
                        .header("X-XSRF-TOKEN", secondCsrfCookie.getValue())
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
        Cookie csrfCookie = fetchCsrfCookie();
        RegisterUserRequest request = new RegisterUserRequest("student@example.com", "password123");
        mockMvc.perform(post("/api/auth/registration")
                        .cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "student@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie sessionCookie = loginResult.getResponse().getCookie("SESSION");

        assert sessionCookie != null;
        mockMvc.perform(post("/api/news")
                        .cookie(sessionCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Title",
                                  "message": "Message"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(4030))
                .andExpect(jsonPath("$.message").value("Доступ запрещен"));
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

    private Cookie fetchCsrfCookie(Cookie... cookies) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/auth/me");
        if (cookies.length > 0) {
            requestBuilder.cookie(cookies);
        }

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andReturn();
        return result.getResponse().getCookie("XSRF-TOKEN");
    }

    private void deleteRedisKeys() {
        Set<String> keys = stringRedisTemplate.keys("jbcd:7a:session-auth:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private String getCookieValue(CookieManager cookieManager) {
        List<HttpCookie> cookies = cookieManager.getCookieStore().get(uri("/"));
        return cookies.stream()
                .filter(cookie -> "XSRF-TOKEN".equals(cookie.getName()))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не нашел cookie " + "XSRF-TOKEN"));
    }
}
