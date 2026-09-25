package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.mapper.NewsMapper;
import com.karandashov.repository.NewsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

/**
 * Слой бизнес логики
 */
@Service // по сути тоже самое что @Component, но с оттенком бизнес-логики
@RequiredArgsConstructor
public class NewsEventService {

    private final NewsEventRepository repository;

    private final NewsMapper mapper;

    @Transactional(readOnly = true)
    public NewsEvent getById(UUID id) {
        NewsEventEntity event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
        return mapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public NewsEventList getAll() {
        List<NewsEvent> events = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .toList();
        return new NewsEventList(events);
    }

    @Transactional
    public NewsEvent save(CreateNewsEventRequest request) {
        NewsEventEntity entity = NewsEventEntity.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }
}
