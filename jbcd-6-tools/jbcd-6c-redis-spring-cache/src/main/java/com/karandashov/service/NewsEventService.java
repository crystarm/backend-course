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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final NewsMapper mapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = "news", key = "#id")
    public UniversalResponse<NewsEvent> getById(UUID id) {
        NewsEventEntity event = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не смог найти новость по id"));
        NewsEvent newsEventDto = mapper.toDto(event);
        return new UniversalResponse<>(newsEventDto);
    }

    @Cacheable(value = "news_list")
    public UniversalResponse<NewsEventList> getAll() {
        List<NewsEventEntity> events = repository.findAll();
        List<NewsEvent> newsEventDtoList = mapper.toDtoList(events);
        NewsEventList newsEventList = new NewsEventList(newsEventDtoList);
        return new UniversalResponse<>(newsEventList);
    }

    @CacheEvict(value = {"news", "news_list"}, allEntries = true, beforeInvocation = true)
    public UniversalResponse<NewsEvent> save(CreateNewsEventRequest request) {
        NewsEventEntity entity = NewsEventEntity.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
        entity = repository.save(entity);
        NewsEvent newsEventDto = mapper.toDto(entity);
        redisTemplate.convertAndSend("news-events", newsEventDto);
        return new UniversalResponse<>(newsEventDto);
    }
}
