package com.karandashov;

import com.karandashov.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.karandashov.Topics.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionNotificationListener {

    private final NotificationProducer producer;

    /*
     * Важно: метод помечен как @Transactional. При наличии KafkaTransactionManager
     * (он автоматически настроен благодаря transaction-id-prefix в properties)
     * контейнер слушателя выполняет чтение сообщения, отправку в OUT-топики
     * и фиксацию offset'а в рамках одной Kafka-транзакции.
     */
    @Transactional
    @KafkaListener(topics = IN_TOPIC, groupId = "NOTIFICATIONS_SERVICE_" + IN_TOPIC)
    public void listener(@Payload NotificationEvent event,
                         @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP, required = false) Long ts,
                         @Header(value = KafkaHeaders.RECEIVED_TOPIC, required = false) String topic,
                         @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
                         @Header(value = KafkaHeaders.OFFSET, required = false) Long offset,
                         @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key
    ) {
        log.info("<-- IN KAFKA TRANSACTION: Получено событие из kafka (topic: {}, partition: {}, offset: {}, ts: {}, key: {}):\n {}",
                topic, partition, offset, ts, key, event);
        producer.send(OUT_TOPIC_EMAIL, event);
        producer.send(OUT_TOPIC_TG, event);
        log.info("--> OUT KAFKA TRANSACTION");
    }
}
