package com.karandashov;

import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewsConfiguration {

    @Bean
    public NewsEventService newsEventService(NewsEventRepository repository) {
        return new NewsEventService(repository);
    }

    @Bean
    public NewsEventRepository newsEventRepository() {
        return new InMemoryNewsEventRepository();
    }
}
