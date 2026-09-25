package com.karandashov;

import lombok.extern.slf4j.Slf4j;

import static com.karandashov.dto.NotificationEvent.TOPIC;

@Slf4j
public class Main {

    public static void main(String[] args) throws InterruptedException {

        PropertiesService propertiesService = new PropertiesService();
        String kafkaBrokersUrls = propertiesService.get("kafka.bootstrap-servers");

        ServiceConsumer serviceConsumer = new ServiceConsumer(kafkaBrokersUrls);
        serviceConsumer.subscribeAsync(TOPIC, "telegram");
        serviceConsumer.subscribeAsync(TOPIC, "telegram");
        serviceConsumer.subscribeAsync(TOPIC, "email");

        Thread.sleep(5000);

        ServiceProducer serviceProducer = new ServiceProducer(kafkaBrokersUrls);
        serviceProducer.start();
    }
}
