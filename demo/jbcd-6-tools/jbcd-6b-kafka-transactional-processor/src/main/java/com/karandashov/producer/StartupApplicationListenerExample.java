package com.karandashov.producer;

import com.karandashov.dto.NotificationEvent;
import com.karandashov.util.EventGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.karandashov.Topics.IN_TOPIC;
import static org.apache.kafka.common.utils.Utils.sleep;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka", name = "start", havingValue = "true")
public class StartupApplicationListenerExample implements ApplicationListener<ContextRefreshedEvent> {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            var event = EventGenerator.generateEvent();
            kafkaTemplate.executeInTransaction(kt ->
                    kt.send(IN_TOPIC, event.getUser(), event)
            );
            sleep(1000);
        }
    }
}