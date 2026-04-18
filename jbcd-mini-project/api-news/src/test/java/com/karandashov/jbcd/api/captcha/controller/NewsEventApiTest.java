package com.karandashov.jbcd.api.captcha.controller;

import com.karandashov.BaseTest;
import com.karandashov.jbcd.api.captcha.dto.NewsEvent;
import com.karandashov.jbcd.api.captcha.dto.NewsEventList;
import com.karandashov.jbcd.api.captcha.dto.request.CreateNewsEventRequest;
import com.karandashov.jbcd.api.captcha.entity.NewsEventEntity;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class NewsEventApiTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrometheusMeterRegistry prometheusMeterRegistry;

    @Autowired
    private NewsEventController newsEventController;

    @Test
    void getAllReturnsNewsFromDatabase() throws Exception {
        NewsEventEntity savedEntity = newsEventRepository.save(new NewsEventEntity(null, "Title", "Message"));

        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].id").value(savedEntity.getId().toString()))
                .andExpect(jsonPath("$.events[0].title").value("Title"))
                .andExpect(jsonPath("$.events[0].message").value("Message"));
    }

    @Test
    void getAllStoresResponseInCache() throws Exception {
        newsEventRepository.save(new NewsEventEntity(null, "Title", "Message"));

        mockMvc.perform(get("/news"))
                .andExpect(status().isOk());

        assertThat(Objects.requireNonNull(cacheManager.getCache("news_list")).get(SimpleKey.EMPTY, NewsEventList.class)).isNotNull();
    }

    @Test
    void getAllReturnsCachedResponseWhenDatabaseIsEmpty() throws Exception {
        NewsEventEntity savedEntity = newsEventRepository.save(new NewsEventEntity(null, "Cached title", "Cached message"));
        newsEventController.getAll();
        newsEventRepository.deleteAll();

        mockMvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events[0].id").value(savedEntity.getId().toString()))
                .andExpect(jsonPath("$.events[0].title").value("Cached title"))
                .andExpect(jsonPath("$.events[0].message").value("Cached message"));
    }

    @Test
    void getByIdReturnsNewsFromDatabase() throws Exception {
        NewsEventEntity savedEntity = newsEventRepository.save(new NewsEventEntity(null, "Title", "Message"));

        mockMvc.perform(get("/news/{id}", savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntity.getId().toString()))
                .andExpect(jsonPath("$.title").value("Title"))
                .andExpect(jsonPath("$.message").value("Message"));
    }

    @Test
    void getByIdStoresResponseInCache() throws Exception {
        NewsEventEntity savedEntity = newsEventRepository.save(new NewsEventEntity(null, "Title", "Message"));

        mockMvc.perform(get("/news/{id}", savedEntity.getId()))
                .andExpect(status().isOk());

        assertThat(Objects.requireNonNull(cacheManager.getCache("news")).get(savedEntity.getId(), NewsEvent.class))
                .isEqualTo(new NewsEvent(savedEntity.getId(), "Title", "Message"));
    }

    @Test
    void getByIdReturnsCachedResponseWhenDatabaseIsEmpty() throws Exception {
        NewsEventEntity savedEntity = newsEventRepository.save(new NewsEventEntity(null, "Cached title", "Cached message"));
        newsEventController.getById(savedEntity.getId());
        newsEventRepository.deleteAll();

        mockMvc.perform(get("/news/{id}", savedEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEntity.getId().toString()))
                .andExpect(jsonPath("$.title").value("Cached title"))
                .andExpect(jsonPath("$.message").value("Cached message"));
    }

    @Test
    void saveCreatesNews() throws Exception {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Breaking", "Something happened");

        mockMvc.perform(post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Breaking"))
                .andExpect(jsonPath("$.message").value("Something happened"));
    }

    @Test
    void saveEvictsNewsListCache() throws Exception {
        Objects.requireNonNull(cacheManager.getCache("news_list")).put(
                SimpleKey.EMPTY,
                new NewsEventList(java.util.List.of(new NewsEvent(UUID.randomUUID(), "Old", "Old message")))
        );

        mockMvc.perform(post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened"))))
                .andExpect(status().isOk());

        assertCacheBecomesEmpty();
    }

    @Test
    void saveUpdatesNewsCountMetric() throws Exception {
        newsEventRepository.save(new NewsEventEntity(null, "Existing", "Already here"));

        mockMvc.perform(post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened"))))
                .andExpect(status().isOk());

        assertThat(prometheusMeterRegistry.get("business.news.count").gauge().value()).isEqualTo(2.0d);
        assertThat(prometheusMeterRegistry.scrape()).contains("business_news_count 2.0");
    }

    @Test
    void getByIdReturns404WhenNewsWasNotFound() throws Exception {
        mockMvc.perform(get("/news/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").value("News not found"));
    }

    @ParameterizedTest
    @MethodSource("invalidCreateRequests")
    void saveReturns400ForInvalidRequest(CreateNewsEventRequest request, String expectedMessagePart) throws Exception {
        mockMvc.perform(post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation error")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(expectedMessagePart)));
    }

    private static Stream<Arguments> invalidCreateRequests() {
        return Stream.of(
                Arguments.of(new CreateNewsEventRequest("", "Valid message"), "title"),
                Arguments.of(new CreateNewsEventRequest("Valid title", ""), "message"),
                Arguments.of(new CreateNewsEventRequest(" ".repeat(101), "Valid message"), "title"),
                Arguments.of(new CreateNewsEventRequest("Valid title", "a".repeat(4001)), "message")
        );
    }

    private void assertCacheBecomesEmpty() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            if (Objects.requireNonNull(cacheManager.getCache("news_list")).get(SimpleKey.EMPTY) == null) {
                return;
            }
            Thread.sleep(50);
        }
        assertThat(Objects.requireNonNull(cacheManager.getCache("news_list")).get(SimpleKey.EMPTY)).isNull();
    }
}
