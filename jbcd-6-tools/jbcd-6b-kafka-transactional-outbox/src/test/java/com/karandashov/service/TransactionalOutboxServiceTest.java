package com.karandashov.service;

import com.karandashov.BaseIntegrationTest;
import com.karandashov.entity.TransactionalOutboxEntity;
import com.karandashov.producer.UniversalProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionalOutboxServiceTest extends BaseIntegrationTest {

    @Autowired
    private TransactionalOutboxService outboxService;

    @MockitoSpyBean
    private UniversalProducer producer;

    @Test
    void testSendEvents_Success() {
        // Given
        outboxService.save("test-topic", "key1", Map.of("data", "val1"));
        outboxService.save("test-topic", "key2", Map.of("data", "val2"));

        // When
        int processedCount = outboxService.sendEvents(10);

        // Then
        assertThat(processedCount).isEqualTo(2);

        List<TransactionalOutboxEntity> allEvents = transactionalOutboxRepository.findAll();
        assertThat(allEvents).hasSize(2);
        assertThat(allEvents).allMatch(TransactionalOutboxEntity::getIsSend);

        verify(producer, times(2)).send(any());
    }

    @Test
    void testSendEvents_BatchSize() {
        // Given
        for (int i = 0; i < 5; i++) {
            outboxService.save("test-topic", "key" + i, Map.of("data", "val" + i));
        }

        // When
        int processedCount = outboxService.sendEvents(3);

        // Then
        assertThat(processedCount).isEqualTo(3);

        long sentCount = transactionalOutboxRepository.findAll().stream().filter(TransactionalOutboxEntity::getIsSend).count();
        assertThat(sentCount).isEqualTo(3);

        verify(producer, times(3)).send(any());
    }

    @Test
    void testSendEvents_ErrorHandling() {
        // Given
        String key = "error-key";
        // Сохраняем два события с одним и тем же ключом
        outboxService.save("test-topic", key, Map.of("data", "val1"));
        outboxService.save("test-topic", key, Map.of("data", "val2"));
        outboxService.save("test-topic", "other-key", Map.of("data", "val3"));

        // Настраиваем продюсер так, чтобы он падал на первом событии
        // Так как порядок гарантируется findByIsSendIsFalseOrderByCreatedDate, 
        // мы можем ожидать, что первое событие будет обработано первым.
        doThrow(new RuntimeException("Kafka error")).doCallRealMethod().when(producer).send(any());

        // When
        // Порядок вызовов: 
        // 1. val1 (error-key) -> ошибка. errorKeys.add("error-key")
        // 2. val2 (error-key) -> пропуск (continue), так как errorKeys содержит "error-key"
        // 3. val3 (other-key) -> успех. sendIds.add(...)
        int processedCount = outboxService.sendEvents(10);

        // Then
        // В методе sendEvents возвращается events.size(), а не количество успешно отправленных.
        // Но val2 был пропущен через continue, поэтому он входит в events.size(), но не в sendIds.
        assertThat(processedCount).isEqualTo(3);

        List<TransactionalOutboxEntity> allEvents = transactionalOutboxRepository.findAll();

        // val3 (other-key) должно быть отправлено
        TransactionalOutboxEntity otherKeyEvent = allEvents.stream()
                .filter(e -> e.getKey().equals("other-key"))
                .findFirst().orElseThrow();
        assertThat(otherKeyEvent.getIsSend()).isTrue();

        // val1 и val2 (error-key) не должны быть отправлены
        List<TransactionalOutboxEntity> errorKeyEvents = allEvents.stream()
                .filter(e -> e.getKey().equals("error-key"))
                .toList();
        assertThat(errorKeyEvents).allMatch(e -> !e.getIsSend());

        // Продюсер должен быть вызван только 2 раза (для val1 и val3)
        // val2 пропускается в цикле до вызова send()
        verify(producer, times(2)).send(any());
    }
}
