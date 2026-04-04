package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.entity.UserEntity;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NewsEventControllerTest extends BaseTest {

    @Autowired
    private NewsEventRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void tearDown() {
        repository.deleteAll();
        userRepository.deleteAll();
        deleteRedisKeys("jbcd:7a:session-auth:*");
    }

    @Test
    void testSaveWithInvalidRequest() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        CreateNewsEventRequest request = new CreateNewsEventRequest("", "");

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testSaveWithMissingTitle() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        CreateNewsEventRequest request = new CreateNewsEventRequest(null, "Message");

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value("title: Заголовок не может быть пустым"));
    }

    @Test
    void testSaveWithMissingMessage() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        CreateNewsEventRequest request = new CreateNewsEventRequest("Title", null);

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value("message: Сообщение не может быть пустым"));
    }

    @Test
    void testSaveWithTitleTooLong() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        String longTitle = "a".repeat(101);
        CreateNewsEventRequest request = new CreateNewsEventRequest(longTitle, "Message");

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value(containsString("size must be between 1 and 100")));
    }

    @Test
    void testSaveWithMaxTitleLength() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        String maxTitle = "a".repeat(100);
        CreateNewsEventRequest request = new CreateNewsEventRequest(maxTitle, "Message");

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value(maxTitle));
    }

    @Test
    void testSaveWithMinTitleLength() throws Exception {
        AuthenticatedClient authenticatedClient = loginTestUser();
        CreateNewsEventRequest request = new CreateNewsEventRequest("a", "Message");

        mockMvc.perform(post("/api/news")
                        .cookie(authenticatedClient.sessionCookie(), authenticatedClient.csrfCookie())
                        .header("X-XSRF-TOKEN", authenticatedClient.csrfCookie().getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("a"));
    }

    @Test
    void testGetAllNews() throws Exception {
        NewsEventEntity entity1 = NewsEventEntity.builder()
                .id(UUID.randomUUID())
                .title("Title 1")
                .message("Message 1")
                .build();
        NewsEventEntity entity2 = NewsEventEntity.builder()
                .id(UUID.randomUUID())
                .title("Title 2")
                .message("Message 2")
                .build();
        repository.save(entity1);
        repository.save(entity2);

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.events", hasSize(2)))
                .andExpect(jsonPath("$.data.events[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data.events[1].title").value("Title 2"));
    }

    @Test
    void testGetNewsById() throws Exception {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = NewsEventEntity.builder()
                .id(id)
                .title("Title")
                .message("Message")
                .build();
        repository.save(entity);

        mockMvc.perform(get("/api/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    void testGetNewsByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/news/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value("Не смог найти новость по id"));
    }

    @Test
    void testSaveRequiresAuthentication() throws Exception {
        Cookie csrfCookie = fetchCsrfCookie();

        mockMvc.perform(post("/api/news")
                        .cookie(csrfCookie)
                        .header("X-XSRF-TOKEN", csrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Title",
                                  "message": "Message"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(4010))
                .andExpect(jsonPath("$.message").value("Требуется аутентификация"));
    }

    private AuthenticatedClient loginTestUser() throws Exception {
        String email = "author@example.com";
        String password = "password123";
        userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .createdAt(Instant.now())
                .build());

        Cookie anonymousCsrfCookie = fetchCsrfCookie();
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .cookie(anonymousCsrfCookie)
                        .header("X-XSRF-TOKEN", anonymousCsrfCookie.getValue())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andReturn();

        Cookie sessionCookie = loginResult.getResponse().getCookie("SESSION");
        Cookie csrfCookie = fetchCsrfCookie(sessionCookie);
        return new AuthenticatedClient(sessionCookie, csrfCookie);
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

    private void deleteRedisKeys(String pattern) {
        var keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    private record AuthenticatedClient(Cookie sessionCookie, Cookie csrfCookie) {
    }
}
