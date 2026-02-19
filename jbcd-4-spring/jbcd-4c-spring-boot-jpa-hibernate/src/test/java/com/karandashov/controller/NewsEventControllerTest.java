package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
public class NewsEventControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final JsonMapper jsonMapper = new JsonMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testSaveWithInvalidRequest() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("", "");

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testSaveWithMissingTitle() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest(null, "Message");

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value("title: Заголовок не может быть пустым"));
    }

    @Test
    void testSaveWithMissingMessage() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Title", null);

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value("message: Сообщение не может быть пустым"));
    }

    @Test
    void testSaveWithTitleTooLong() throws Exception {
        String longTitle = "a".repeat(101);
        CreateNewsEventRequest request = new CreateNewsEventRequest(longTitle, "Message");

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4001))
                .andExpect(jsonPath("$.message").value(containsString("size must be between 1 and 100")));
    }

    @Test
    void testSaveWithMaxTitleLength() throws Exception {
        String maxTitle = "a".repeat(100);
        CreateNewsEventRequest request = new CreateNewsEventRequest(maxTitle, "Message");

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value(maxTitle));
    }

    @Test
    void testSaveWithMinTitleLength() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("a", "Message");

        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.title").value("a"));
    }
}
