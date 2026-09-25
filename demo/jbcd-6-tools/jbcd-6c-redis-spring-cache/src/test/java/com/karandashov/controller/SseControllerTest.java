package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class SseControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoSpyBean
    private SseController sseController;

    @Test
    void testSseSubscription() throws Exception {
        // Подключаемся к SSE
        MvcResult mvcResult = mockMvc.perform(get("/news/listen"))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        // Создаем новость, что должно триггернуть SSE событие через Redis Pub/Sub
        CreateNewsEventRequest request = new CreateNewsEventRequest("SSE Title", "SSE Message");
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Проверяем, что broadcast был вызван (через NewsEventRedisListener)
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(sseController, timeout(5000)).broadcast(eventCaptor.capture());

        NewsEvent capturedEvent = (NewsEvent) eventCaptor.getValue();
        assertThat(capturedEvent.getTitle()).isEqualTo("SSE Title");
        assertThat(capturedEvent.getMessage()).isEqualTo("SSE Message");

        // Проверяем, что данные действительно попали в ответ (MockMvc захватывает вывод SseEmitter)
        String sseResponse = mvcResult.getResponse().getContentAsString();
        assertThat(sseResponse).contains("data:{\"id\":\"" + capturedEvent.getId() + "\",\"title\":\"SSE Title\",\"message\":\"SSE Message\"}");
    }
}
