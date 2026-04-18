package com.karandashov.jbcd.api.captcha.configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    public MetricsConfiguration(MeterRegistry meterRegistry, CaptchaRequestRepository repository) {
        Gauge.builder("business.captcha.requests.count", repository, CaptchaRequestRepository::count)
                .description("Current number of requested captcha")
                .register(meterRegistry);
    }
}
