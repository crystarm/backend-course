package com.karandashov.service;

import com.karandashov.dto.request.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.exceptions.BusinessError;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.karandashov.repository.NewsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class NewsEventService {

    private final NewsEventRepository repository;

    @Cacheable(value = "news", key = "#id")
    public NewsEvent getById(UUID id) {
        NewsEventEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(BusinessError.NEWS_NOT_FOUND));
        return map(entity);
    }

    @Cacheable(value = "news_list")
    public NewsEventList getAll() {
        List<NewsEvent> events = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(this::map)
                .toList();
        return new NewsEventList(events);
    }

    @CacheEvict(value = {"news_list"}, allEntries = true)
    public NewsEvent save(CreateNewsEventRequest request) {
        NewsEventEntity entity = new NewsEventEntity(null, request.getTitle(), request.getMessage());
        entity = repository.save(entity);
        return map(entity);
    }

    private NewsEvent map(NewsEventEntity entity) {
        return new NewsEvent(entity.getId(), entity.getTitle(), entity.getMessage());
    }
}
