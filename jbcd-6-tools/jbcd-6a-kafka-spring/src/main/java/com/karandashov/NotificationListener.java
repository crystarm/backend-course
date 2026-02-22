package com.karandashov;

import com.karandashov.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.karandashov.dto.NotificationEvent.TOPIC;

@Slf4j
@Component
public class NotificationListener {

    @KafkaListener(topics = TOPIC, groupId = "SECURITY_SERVICE_" + TOPIC)
    public void listener(@Payload NotificationEvent event,
                         @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP, required = false) Long ts,
                         @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
                         @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                         @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
                         @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("<-- Получено событие из kafka (topic: {}, partition: {}, offset: {}, ts: {}, key: {}):\n {}",
                topic, partition, offset, ts, key, event);
    }
}
