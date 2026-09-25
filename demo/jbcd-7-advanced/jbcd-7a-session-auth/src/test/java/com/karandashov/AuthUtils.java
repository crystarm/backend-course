package com.karandashov;

import com.karandashov.entity.UserEntity;
import com.karandashov.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class AuthUtils {

    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";
    public static final String SESSION_COOKIE_NAME = "SESSION";
    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private AuthUtils() {
    }

    public static Cookie fetchCsrfCookie(MockMvc mockMvc, Cookie... cookies) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/api/auth/me");
        if (cookies.length > 0) {
            requestBuilder.cookie(cookies);
        }

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(cookie().exists(CSRF_COOKIE_NAME))
                .andReturn();
        return requireCookie(result, CSRF_COOKIE_NAME);
    }

    public static AuthenticatedClient loginTestUser(
            MockMvc mockMvc,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) throws Exception {
        return loginTestUser(mockMvc, userRepository, passwordEncoder, "author@example.com", "password123");
    }

    public static AuthenticatedClient loginTestUser(
            MockMvc mockMvc,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password
    ) throws Exception {
        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(Instant.now())
                .build());

        Cookie anonymousCsrfCookie = fetchCsrfCookie(mockMvc);
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .cookie(anonymousCsrfCookie)
                        .header(CSRF_HEADER_NAME, anonymousCsrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        Cookie sessionCookie = requireCookie(loginResult, SESSION_COOKIE_NAME);
        Cookie csrfCookie = fetchCsrfCookie(mockMvc, sessionCookie);
        return new AuthenticatedClient(sessionCookie, csrfCookie, email, password);
    }

    public static Cookie requireCookie(MvcResult mvcResult, String cookieName) {
        Cookie cookie = mvcResult.getResponse().getCookie(cookieName);
        assertNotNull(cookie, "Не нашел cookie " + cookieName);
        return cookie;
    }

    public static URI uri(int port, String path) {
        return URI.create("http://localhost:" + port + path);
    }

    public static String getCookieValue(CookieManager cookieManager, URI uri, String cookieName) {
        List<HttpCookie> cookies = cookieManager.getCookieStore().get(uri);
        return cookies.stream()
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Не нашел cookie " + cookieName));
    }

    public record AuthenticatedClient(Cookie sessionCookie, Cookie csrfCookie, String email, String password) {
    }
}
