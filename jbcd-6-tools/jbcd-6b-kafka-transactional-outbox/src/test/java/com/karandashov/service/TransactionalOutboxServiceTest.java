package com.karandashov.service;

import com.karandashov.BaseIntegrationTest;
import com.karandashov.entity.TransactionalOutboxEntity;
import com.karandashov.producer.UniversalProducer;
import com.karandashov.repository.TransactionalOutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class TransactionalOutboxServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionalOutboxService outboxService;

    @Autowired
    private TransactionalOutboxRepository repository;

    @MockitoBean
    private UniversalProducer producer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testSave() {
        String topic = "test-topic";
        String key = "test-key";
        Map<String, String> payload = Map.of("message", "hello");

        outboxService.save(topic, key, payload);

        List<TransactionalOutboxEntity> entities = repository.findAll();
        assertEquals(1, entities.size());
        TransactionalOutboxEntity entity = entities.getFirst();
        assertEquals(topic, entity.getTopic());
        assertEquals(key, entity.getKey());
        assertTrue(entity.getPayload().contains("hello"));
        assertFalse(entity.getIsSend());
    }

    @Test
    void testSendEventsSuccess() {
        outboxService.save("topic1", "key1", "payload1");
        outboxService.save("topic2", "key2", "payload2");

        outboxService.sendEvents(PageRequest.of(0, 10));

        verify(producer, times(2)).send(any(TransactionalOutboxEntity.class));

        List<TransactionalOutboxEntity> entities = repository.findAll();
        assertEquals(2, entities.size());
        assertTrue(entities.stream().allMatch(TransactionalOutboxEntity::getIsSend));
    }

    @Test
    void testSendEventsPartialFailure() {
        outboxService.save("topic1", "key1", "payload1");
        outboxService.save("topic2", "key2", "payload2");

        List<TransactionalOutboxEntity> entitiesBefore = repository.findAll();
        // Сортировка по дате создания в репозитории
        entitiesBefore.sort(Comparator.comparing(TransactionalOutboxEntity::getCreatedDate));

        UUID id1 = entitiesBefore.get(0).getId();
        UUID id2 = entitiesBefore.get(1).getId();

        // Настраиваем мок так, чтобы для первой записи была ошибка
        doThrow(new RuntimeException("Kafka error")).when(producer).send(argThat(entity -> entity.getId().equals(id1)));

        outboxService.sendEvents(PageRequest.of(0, 10));

        verify(producer, times(2)).send(any(TransactionalOutboxEntity.class));

        TransactionalOutboxEntity entity1 = repository.findById(id1).orElseThrow();
        TransactionalOutboxEntity entity2 = repository.findById(id2).orElseThrow();

        assertFalse(entity1.getIsSend(), "Первое сообщение не должно быть помечено как отправленное");
        assertTrue(entity2.getIsSend(), "Второе сообщение должно быть помечено как отправленное");
    }

    @Test
    void testPagination() {
        int count = 15;
        for (int i = 0; i < count; i++) {
            outboxService.save("topic", "key" + i, "payload" + i);
        }

        var nextPage = outboxService.sendEvents(PageRequest.of(0, 10));

        verify(producer, times(10)).send(any(TransactionalOutboxEntity.class));
        assertTrue(nextPage.isPaged());
        assertEquals(0, nextPage.getPageNumber(), "Должна вернуться та же страница, так как данные 'сдвинулись'");

        long sentCount = repository.findAll().stream().filter(TransactionalOutboxEntity::getIsSend).count();
        assertEquals(10, sentCount);

        // Отправляем оставшиеся
        var lastPage = outboxService.sendEvents(nextPage);
        verify(producer, times(15)).send(any(TransactionalOutboxEntity.class));
        assertFalse(lastPage.isPaged(), "После обработки всех записей должно вернуться unpaged");

        sentCount = repository.findAll().stream().filter(TransactionalOutboxEntity::getIsSend).count();
        assertEquals(15, sentCount);
    }
}
