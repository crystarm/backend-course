package com.karandashov.service;

import com.karandashov.entity.TransactionalOutboxEntity;
import com.karandashov.repository.TransactionalOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionalOutboxService {

    private final ObjectMapper objectMapper;

    private final TransactionalOutboxRepository transactionalOutboxRepository;

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
}
