package com.karandashov.controller;

import com.karandashov.BaseTest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class NewsEventControllerTest extends BaseTest {

    @MockitoSpyBean
    private NewsEventRepository repository;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void tearDown() {
        repository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        clearInvocations(repository);
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

        mockMvc.perform(get("/news"))
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

        mockMvc.perform(get("/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.title").value("Title"));
    }

    @Test
    void testGetNewsByIdNotFound() throws Exception {
        mockMvc.perform(get("/news/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(4000))
                .andExpect(jsonPath("$.message").value("Не смог найти новость по id"));
    }

    @Test
    void testGetNewsByIdCaching() throws Exception {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = NewsEventEntity.builder()
                .id(id)
                .title("Cached Title")
                .message("Cached Message")
                .build();
        repository.save(entity);
        clearInvocations(repository);

        // Первый вызов - должен пойти в репозиторий
        mockMvc.perform(get("/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Cached Title"));

        verify(repository, times(1)).findById(id);

        // Второй вызов - должен взять из кэша
        mockMvc.perform(get("/news/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Cached Title"));

        // verify что findById все еще был вызван 1 раз (с момента clearInvocations)
        verify(repository, times(1)).findById(id);
    }

    @Test
    void testCacheEvictionOnSave() throws Exception {
        clearInvocations(repository);
        // Заполняем кэш getAll
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk());

        verify(repository, times(1)).findAll();

        // Повторный вызов должен прийти из кэша.
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk());

        verify(repository, times(1)).findAll();

        // Сохраняем новую новость
        CreateNewsEventRequest request = new CreateNewsEventRequest("New Title", "New Message");
        mockMvc.perform(post("/news")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Вызываем getAll снова - кэш должен быть пуст, вызов репозитория повторится
        mockMvc.perform(get("/news"))
                .andExpect(status().isOk());

        verify(repository, times(2)).findAll();
    }
}
