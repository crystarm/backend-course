package com.karandashov.configuration;

import com.karandashov.repository.NewsEventRepository;
import com.karandashov.validator.impl.NewsEventMessageValidator;
import com.karandashov.validator.impl.NewsEventTitleExistValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfiguration {

    @Bean
    public NewsEventMessageValidator newsEventMessageValidator() {
        return new NewsEventMessageValidator();
    }

    @Bean
    public NewsEventTitleExistValidator newsEventTitleExistValidator(NewsEventRepository repository) {
        return new NewsEventTitleExistValidator(repository);
    }
}
