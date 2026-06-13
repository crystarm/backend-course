package com.karandashov.jbcd.api.captcha.service;

import com.karandashov.jbcd.api.captcha.configuration.MetricsConfiguration;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricService {

    private final Map<MetricsConfiguration.CaptchaEvent, Counter> captchaCounters;

    public void incrementCaptchaCounter(MetricsConfiguration.CaptchaEvent event) {
        captchaCounters.get(event).increment();
    }
}
