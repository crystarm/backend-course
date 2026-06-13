package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.mapper.NewsMapper;
import com.karandashov.repository.NewsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Слой бизнес логики
 */
@Service // по сути тоже самое что @Component, но с оттенком бизнес-логики
@RequiredArgsConstructor
public class NewsEventService {

    private final NewsEventRepository repository;

    private final NewsMapper mapper;

    @Transactional(readOnly = true)
    public UniversalResponse<NewsEvent> getById(UUID id) {
        NewsEventEntity event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
        NewsEvent newsEventDto = mapper.toDto(event);
        return new UniversalResponse<>(newsEventDto);
    }

    @Transactional(readOnly = true)
    public UniversalResponse<NewsEventList> getAll() {
        List<NewsEventEntity> events = repository.findAll();
        List<NewsEvent> newsEventDtoList = mapper.toDtoList(events);
        NewsEventList newsEventList = new NewsEventList(newsEventDtoList);
        return new UniversalResponse<>(newsEventList);
    }

    @Transactional
    public UniversalResponse<NewsEvent> save(CreateNewsEventRequest request) {
        NewsEventEntity entity = NewsEventEntity.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
        entity = repository.save(entity);
        NewsEvent newsEventDto = mapper.toDto(entity);
        return new UniversalResponse<>(newsEventDto);
    }
}
