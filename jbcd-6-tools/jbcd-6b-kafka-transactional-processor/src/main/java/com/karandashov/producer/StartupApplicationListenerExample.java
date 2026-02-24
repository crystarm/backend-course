package com.karandashov.producer;

import com.karandashov.util.EventGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import static org.apache.kafka.common.utils.Utils.sleep;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.kafka", name = "start", havingValue = "true")
public class StartupApplicationListenerExample implements ApplicationListener<ContextRefreshedEvent> {

    public final NotificationProducer producer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            var event = EventGenerator.generateEvent();
            producer.send(event);
            sleep(1000);
        }
    }
}