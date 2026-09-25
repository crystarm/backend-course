package com.karandashov;

import com.karandashov.util.EventGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.karandashov.dto.NotificationEvent.TOPIC;

@Slf4j
@RequiredArgsConstructor
public class ServiceProducer {

    private static final JsonMapper mapper = new JsonMapper();

    private final String kafkaBrokersUrls;

    public void start() {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(generateProducerProperties())) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                generateAndSend(producer);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("Error during sending message", e);
            throw new RuntimeException(e);
        }
    }

    private void generateAndSend(KafkaProducer<String, String> producer) throws ExecutionException, InterruptedException {
        var event = EventGenerator.generateEvent();
        String eventJson = mapper.writeValueAsString(event);

        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, event.getUser(), eventJson);
        RecordMetadata response = producer.send(record).get();

        log.info("-> send to topic: {}, partition: {}, offset: {}, body: {}",
                response.topic(), response.partition(), response.offset(), eventJson);
    }

    private Properties generateProducerProperties() {
        Properties producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersUrls);
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // можно еще поставить 0 или 1
        return producerProperties;
    }
}
