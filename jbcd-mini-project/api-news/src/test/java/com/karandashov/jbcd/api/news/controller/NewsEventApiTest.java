package com.karandashov.jbcd.api.news.controller;

import com.karandashov.jbcd.api.news.BaseTest;
import com.karandashov.jbcd.api.news.dto.NewsEvent;
import com.karandashov.jbcd.api.news.dto.NewsEventList;
import com.karandashov.jbcd.api.news.dto.request.CreateNewsEventRequest;
import com.karandashov.jbcd.api.news.entity.NewsEventEntity;
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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        UUID captchaId = UUID.randomUUID();
        stubSolvedCaptcha(captchaId);
        CreateNewsEventRequest request = new CreateNewsEventRequest("Breaking", "Something happened", captchaId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Breaking"))
                .andExpect(jsonPath("$.message").value("Something happened"));

        captchaApiMock().verify(postRequestedFor(urlEqualTo(captchaCheckPath(captchaId))));
    }

    @Test
    void savePropagatesTraceparentHeaderToCaptchaService() throws Exception {
        UUID captchaId = UUID.randomUUID();
        stubSolvedCaptcha(captchaId);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isOk());

        captchaApiMock().verify(postRequestedFor(urlEqualTo(captchaCheckPath(captchaId)))
                .withHeader("traceparent", matching(".+")));
    }

    @Test
    void saveEvictsNewsListCache() throws Exception {
        UUID captchaId = UUID.randomUUID();
        stubSolvedCaptcha(captchaId);
        Objects.requireNonNull(cacheManager.getCache("news_list")).put(
                SimpleKey.EMPTY,
                new NewsEventList(java.util.List.of(new NewsEvent(UUID.randomUUID(), "Old", "Old message")))
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isOk());

        assertCacheBecomesEmpty();
    }

    @Test
    void saveUpdatesNewsCountMetric() throws Exception {
        UUID captchaId = UUID.randomUUID();
        stubSolvedCaptcha(captchaId);
        newsEventRepository.save(new NewsEventEntity(null, "Existing", "Already here"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isOk());

        assertThat(prometheusMeterRegistry.get("business.news.count").gauge().value()).isEqualTo(2.0d);
        assertThat(prometheusMeterRegistry.scrape()).contains("business_news_count 2.0");
    }

    @Test
    void saveReturns400WhenCaptchaWasNotFoundOrExpired() throws Exception {
        UUID captchaId = UUID.randomUUID();
        stubCaptchaError(captchaId, 404, 2000, "Captcha not found or expired");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("Captcha not found or expired"));
    }

    @Test
    void saveReturns400WhenCaptchaWasNotSolved() throws Exception {
        UUID captchaId = UUID.randomUUID();
        stubCaptchaError(captchaId, 400, 2001, "Captcha is not solved");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1002))
                .andExpect(jsonPath("$.message").value("Captcha is not solved"));
    }

    @Test
    void saveReturns502WhenCaptchaCheckFailed() throws Exception {
        UUID captchaId = UUID.randomUUID();
        captchaApiMock().stubFor(post(urlEqualTo(captchaCheckPath(captchaId)))
                .willReturn(aResponse().withStatus(500)));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", captchaId))))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value(1003))
                .andExpect(jsonPath("$.message").value("Failed to check captcha"));
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
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation error")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(expectedMessagePart)));
    }

    @Test
    void saveDoesNotCallCaptchaServiceWhenRequestWasInvalid() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/news")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new CreateNewsEventRequest("Breaking", "Something happened", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(4000));

        captchaApiMock().verify(0, postRequestedFor(urlPathMatching("/internal/captcha/.+/checkAndExpire")));
    }

    private static Stream<Arguments> invalidCreateRequests() {
        return Stream.of(
                Arguments.of(new CreateNewsEventRequest("", "Valid message", UUID.randomUUID()), "title"),
                Arguments.of(new CreateNewsEventRequest("Valid title", "", UUID.randomUUID()), "message"),
                Arguments.of(new CreateNewsEventRequest(" ".repeat(101), "Valid message", UUID.randomUUID()), "title"),
                Arguments.of(new CreateNewsEventRequest("Valid title", "a".repeat(4001), UUID.randomUUID()), "message"),
                Arguments.of(new CreateNewsEventRequest("Valid title", "Valid message", null), "solvedCaptchaId")
        );
    }

    private void stubSolvedCaptcha(UUID captchaId) {
        captchaApiMock().stubFor(post(urlEqualTo(captchaCheckPath(captchaId)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody("{\"valid\":true}")));
    }

    private void stubCaptchaError(UUID captchaId, int status, int code, String message) {
        captchaApiMock().stubFor(post(urlEqualTo(captchaCheckPath(captchaId)))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", APPLICATION_JSON.toString())
                        .withBody("{\"code\":" + code + ",\"message\":\"" + message + "\"}")));
    }

    private String captchaCheckPath(UUID captchaId) {
        return "/internal/captcha/" + captchaId + "/checkAndExpire";
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
