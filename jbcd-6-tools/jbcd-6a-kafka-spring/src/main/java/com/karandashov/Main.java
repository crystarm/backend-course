package com.karandashov;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.karandashov.dto.NotificationEvent.TOPIC;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.karandashov")
public class Main {

    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

        String kafkaBrokersUrls = System.getenv("BOOTSTRAP_SERVERS");
        if (kafkaBrokersUrls == null) {
            log.error("Не заданы переменные окружения!");
            return;
        }

        ServiceConsumer serviceConsumer = new ServiceConsumer(kafkaBrokersUrls);
        serviceConsumer.subscribeAsync(TOPIC, "telegram");
        serviceConsumer.subscribeAsync(TOPIC, "telegram");
        serviceConsumer.subscribeAsync(TOPIC, "email");

        Thread.sleep(5000);

        ServiceProducer serviceProducer = new ServiceProducer(kafkaBrokersUrls);
        serviceProducer.start();
    }
}
