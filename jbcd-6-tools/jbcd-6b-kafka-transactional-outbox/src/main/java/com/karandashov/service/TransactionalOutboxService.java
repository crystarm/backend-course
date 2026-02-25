package com.karandashov.service;

import com.karandashov.entity.TransactionalOutboxEntity;
import com.karandashov.producer.UniversalProducer;
import com.karandashov.repository.TransactionalOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionalOutboxService {

    public static final int BATCH_SIZE = 10;

    private final ObjectMapper objectMapper;

    private final TransactionalOutboxRepository transactionalOutboxRepository;

    private final UniversalProducer producer;

    @Transactional(propagation = Propagation.REQUIRED)
    public void save(String topic, String key, Object object) {
        TransactionalOutboxEntity event = TransactionalOutboxEntity.builder()
                .id(UUID.randomUUID())
                .topic(topic)
                .key(key)
                .payload(objectMapper.writeValueAsString(object))
                .build();
        transactionalOutboxRepository.save(event);
    }

    @Transactional
    public void sendEvents() {
        var events = transactionalOutboxRepository.findByIsSendIsFalseOrderByCreatedDate(Limit.of(BATCH_SIZE));
        if (events.isEmpty()) {
            return;
        }
        Set<UUID> sendIds = new HashSet<>(events.size());
        Set<String> errorKeys = new HashSet<>(events.size());
        for (TransactionalOutboxEntity event : events) {
            if (errorKeys.contains(event.getKey())) {
                continue;
            }
            var result = send(event);
            if (result) {
                sendIds.add(event.getId());
            } else {
                errorKeys.add(event.getKey());
            }
        }
        if (!sendIds.isEmpty()) {
            transactionalOutboxRepository.markSent(sendIds);
        }
    }

    private boolean send(TransactionalOutboxEntity entity) {
        try {
            producer.send(entity);
            return true;
        } catch (Exception ignored) {
            // проглотил ошибку, потому что ошибка отправка одной записи не должна ломать весь процесс
            return false;
        }
    }
}
