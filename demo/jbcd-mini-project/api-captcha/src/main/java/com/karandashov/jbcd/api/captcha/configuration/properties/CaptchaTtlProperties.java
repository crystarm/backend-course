package com.karandashov.jbcd.api.captcha.configuration.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "captcha.ttl")
public record CaptchaTtlProperties(
        @NotNull Duration created,
        @NotNull Duration solved
) {
}
