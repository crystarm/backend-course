package com.karandashov.service;

import com.karandashov.entity.TransactionalOutboxEntity;
import com.karandashov.producer.UniversalProducer;
import com.karandashov.repository.TransactionalOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Pageable sendEvents(Pageable page) {
        var slice = transactionalOutboxRepository.findByIsSendIsFalseOrderByCreatedDate(page);
        if (slice.isEmpty()) {
            return Pageable.unpaged();
        }
        Set<UUID> sendIds = new HashSet<>(page.getPageSize());
        slice.forEach(entity -> {
            var result = send(entity);
            if (result) {
                sendIds.add(entity.getId());
            }
        });
        if (!sendIds.isEmpty()) {
            transactionalOutboxRepository.markSent(sendIds);
        }
        return slice.hasNext() ? page : Pageable.unpaged();
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
