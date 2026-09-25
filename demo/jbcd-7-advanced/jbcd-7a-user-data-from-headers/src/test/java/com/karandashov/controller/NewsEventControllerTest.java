package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.entity.NewsEventEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class NewsEventControllerTest extends BaseTest {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testSaveWithInvalidRequest() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("", "");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation.error"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testSaveWithMissingTitle() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest(null, "Message");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation.error"))
                .andExpect(jsonPath("$.message").value("title: Заголовок не может быть пустым"));
    }

    @Test
    void testSaveWithMissingMessage() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Title", null);

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation.error"))
                .andExpect(jsonPath("$.message").value("message: Сообщение не может быть пустым"));
    }

    @Test
    void testSaveWithTitleTooLong() throws Exception {
        String longTitle = "a".repeat(101);
        CreateNewsEventRequest request = new CreateNewsEventRequest(longTitle, "Message");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation.error"))
                .andExpect(jsonPath("$.message").value(containsString("size must be between 1 and 100")));
    }

    @Test
    void testSaveWithMaxTitleLength() throws Exception {
        String maxTitle = "a".repeat(100);
        CreateNewsEventRequest request = new CreateNewsEventRequest(maxTitle, "Message");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(maxTitle));
    }

    @Test
    void testSaveWithMinTitleLength() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("a", "Message");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("a"));
    }

    @Test
    void testSaveSuccessful() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Новая новость", "Текст новости");

        mockMvc.perform(post("/api/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Новая новость"))
                .andExpect(jsonPath("$.message").value("Текст новости"));
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
        newsEventRepository.save(entity1);
        newsEventRepository.save(entity2);

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events", hasSize(2)))
                .andExpect(jsonPath("$.events[0].title").value("Title 1"))
                .andExpect(jsonPath("$.events[1].title").value("Title 2"));
    }

    @Test
    void testGetNewsById() throws Exception {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = NewsEventEntity.builder()
                .id(id)
                .title("Title")
                .message("Message")
                .build();
        newsEventRepository.save(entity);

        mockMvc.perform(get("/api/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testGetNewsByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/news/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("news.not.found"))
                .andExpect(jsonPath("$.message").value("Не смог найти новость по id"));
    }
}
