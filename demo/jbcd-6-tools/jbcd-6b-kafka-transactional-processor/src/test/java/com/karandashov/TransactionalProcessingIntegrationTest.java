package com.karandashov;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashov.dto.NotificationEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static com.karandashov.Topics.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TransactionalProcessingIntegrationTest {

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.7"));

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Test
    void endToEnd_processing_happens_in_single_transaction() {
        // send event to input topic
        NotificationEvent event = new NotificationEvent(42, "user-42");
        // Отправляем входящее сообщение в отдельной транзакции продьюсера
        assertDoesNotThrow(() -> kafkaTemplate.executeInTransaction(kt -> {
            try {
                kt.send(IN_TOPIC, event.getUser(), event).get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }));

        // create consumer for out topics with read_committed isolation
        Properties props = new Properties();
        props.put("bootstrap.servers", kafka.getBootstrapServers());
        props.put("group.id", "test-consumer-" + UUID.randomUUID());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("isolation.level", "read_committed");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                props,
                new StringDeserializer(),
                new StringDeserializer()
        )) {
            consumer.subscribe(Arrays.asList(OUT_TOPIC_EMAIL, OUT_TOPIC_TG));

            Map<String, List<NotificationEvent>> received = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            long deadline = System.currentTimeMillis() + 20_000; // up to 20s
            while (System.currentTimeMillis() < deadline &&
                    (!received.containsKey(OUT_TOPIC_EMAIL) || !received.containsKey(OUT_TOPIC_TG))) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (ConsumerRecord<String, String> rec : records) {
                    try {
                        NotificationEvent ev = mapper.readValue(rec.value(), NotificationEvent.class);
                        received.computeIfAbsent(rec.topic(), t -> new ArrayList<>()).add(ev);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            assertTrue(received.containsKey(OUT_TOPIC_EMAIL), "EMAIL topic should receive an event");
            assertTrue(received.containsKey(OUT_TOPIC_TG), "TG topic should receive an event");

            assertEquals(1, received.get(OUT_TOPIC_EMAIL).size());
            assertEquals(1, received.get(OUT_TOPIC_TG).size());

            assertEquals(event, received.get(OUT_TOPIC_EMAIL).getFirst());
            assertEquals(event, received.get(OUT_TOPIC_TG).getFirst());
        }
    }
}
