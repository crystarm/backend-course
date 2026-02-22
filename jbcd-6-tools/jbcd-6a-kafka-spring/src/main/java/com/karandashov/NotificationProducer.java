package com.karandashov;

import com.karandashov.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.karandashov.dto.NotificationEvent.TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void send(NotificationEvent event) {
        try {
            var response = kafkaTemplate.send(TOPIC, event.getUser(), event).get().getRecordMetadata();
            log.info("--> Отправлено сообщение: {}, partition: {}, offset: {}, body: {}",
                    response.topic(), response.partition(), response.offset(), event);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения", e);
        }
    }
}
