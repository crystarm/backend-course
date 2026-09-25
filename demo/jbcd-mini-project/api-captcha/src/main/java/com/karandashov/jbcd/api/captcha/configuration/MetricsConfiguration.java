package com.karandashov.jbcd.api.captcha.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MetricsConfiguration {

    @Bean
    public Map<CaptchaEvent, Counter> captchaCounters(MeterRegistry meterRegistry) {
        Map<CaptchaEvent, Counter> counters = new HashMap<>(CaptchaEvent.values().length);
        for (CaptchaEvent event : CaptchaEvent.values()) {
            Counter counter = Counter.builder("business.captcha.count")
                    .description("Counter of captcha")
                    .tag("event", event.name())
                    .register(meterRegistry);
            counters.put(event, counter);
        }
        return counters;
    }

    public enum CaptchaEvent {
        CREATED,
        SOLVED,
        MISMATCH,
        SUCCESS_EXPIRE
    }
}
