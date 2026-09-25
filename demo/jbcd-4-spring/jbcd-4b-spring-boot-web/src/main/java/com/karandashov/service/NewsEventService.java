package com.karandashov.service;

import com.karandashov.aop.ClearCache;
import com.karandashov.aop.UseCache;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.NewsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Слой бизнес логики
 */
@Service // по сути тоже самое что @Component, но с оттенком бизнес-логики
@RequiredArgsConstructor
public class NewsEventService {

    private final NewsEventRepository repository;

    public NewsEvent getById(UUID id) {
        NewsEvent event = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
        return map(event);
    }

    @UseCache
    public NewsEventList getAll() {
        return map(repository.getAll());
    }

    @ClearCache
    public NewsEvent save(CreateNewsEventRequest request) {
        NewsEvent newsEvent = new NewsEvent(UUID.randomUUID(), request.getTitle(), request.getMessage());
        repository.save(newsEvent);
        return map(newsEvent);
    }

    private NewsEvent map(NewsEvent newsEvent) {
        return newsEvent;
    }

    private NewsEventList map(List<NewsEvent> newsEventList) {
        return new NewsEventList(newsEventList);
    }
}
