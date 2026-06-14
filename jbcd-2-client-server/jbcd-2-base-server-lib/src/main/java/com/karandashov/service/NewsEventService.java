package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.NewsEventRepository;

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

    public NewsEvent getById(UUID id) {
        return repository.getById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
    }

    public NewsEventList getAll() {
        return new NewsEventList(repository.getAll());
    }

    public NewsEvent save(CreateNewsEventRequest request) {
        NewsEvent newsEvent = new NewsEvent(UUID.randomUUID(), request.getTitle(), request.getMessage());
        repository.save(newsEvent);
        return newsEvent;
    }
}
