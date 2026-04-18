package com.karandashov.jbcd.api.captcha.configuration;

import com.karandashov.jbcd.api.captcha.repository.NewsEventRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    public MetricsConfiguration(MeterRegistry meterRegistry, NewsEventRepository repository) {
        Gauge.builder("business.news.count", repository, NewsEventRepository::count)
                .description("Current number of published news")
                .register(meterRegistry);
    }
}
