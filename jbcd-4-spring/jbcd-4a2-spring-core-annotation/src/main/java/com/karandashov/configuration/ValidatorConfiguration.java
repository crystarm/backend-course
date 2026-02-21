package com.karandashov.configuration;

import com.karandashov.validator.impl.NewsEventMessageValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfiguration {

    @Bean
    public NewsEventMessageValidator newsEventMessageValidator() {
        return new NewsEventMessageValidator();
    }
}
