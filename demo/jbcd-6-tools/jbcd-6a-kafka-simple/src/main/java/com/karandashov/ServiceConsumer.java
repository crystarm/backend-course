package com.karandashov;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class ServiceConsumer {

    private final String kafkaBrokersUrls;

    public void subscribeAsync(String topic, String group) {
        CompletableFuture.runAsync(() -> subscribe(topic, group));
    }

    private void subscribe(String topic, String group) {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(generateConsumerProperties(group))) {
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("<- receive {} from topic: {}, partition: {}, offset: {}, body: {}",
                            group, record.topic(), record.partition(), record.offset(), record.value());
                }
            }
        } catch (Exception e) {
            log.error("Error during consume message", e);
            throw new RuntimeException(e);
        }
    }

    private Properties generateConsumerProperties(String group) {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokersUrls);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // можно еще поставить earliest или none
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        return consumerProperties;
    }
}
