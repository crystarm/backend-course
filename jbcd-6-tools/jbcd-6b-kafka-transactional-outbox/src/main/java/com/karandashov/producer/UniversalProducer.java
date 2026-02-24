package com.karandashov.producer;

import com.karandashov.entity.TransactionalOutboxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UniversalProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(TransactionalOutboxEntity entity) {
        try {
            var response = kafkaTemplate.send(entity.getTopic(), entity.getKey(), entity.getPayload()).get().getRecordMetadata();
            log.info("--> Отправлено сообщение: {}, partition: {}, offset: {}, body: {}",
                    response.topic(), response.partition(), response.offset(), entity.getPayload());
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения", e);
            throw new RuntimeException(e);
        }
    }
}