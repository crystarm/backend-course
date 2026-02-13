package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.NewsEventRepository;

import java.util.List;
import java.util.UUID;

/**
 * Слой бизнес логики, который работает с некоторым репозиторием.
 * Конкретная реализация репозитория не важна, главное чтоб она подчинялась интерфейсу {@link NewsEventRepository}
 */
public class NewsEventService {

    private final NewsEventRepository repository;

    public NewsEventService(NewsEventRepository repository) {
        this.repository = repository;
    }

    public UniversalResponse<NewsEvent> getById(UUID id) {
        NewsEvent event = repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
        return map(event);
    }

    public UniversalResponse<NewsEventList> getAll() {
        return map(repository.getAll());
    }

    public UniversalResponse<NewsEvent> save(CreateNewsEventRequest request) {
        NewsEvent newsEvent = new NewsEvent(UUID.randomUUID(), request.getTitle(), request.getMessage());
        repository.save(newsEvent);
        return map(newsEvent);
    }

    private UniversalResponse<NewsEvent> map(NewsEvent newsEvent) {
        return new UniversalResponse<>(newsEvent);
    }

    private UniversalResponse<NewsEventList> map(List<NewsEvent> newsEventList) {
        NewsEventList data = new NewsEventList(newsEventList);
        return new UniversalResponse<>(data);
    }
}
