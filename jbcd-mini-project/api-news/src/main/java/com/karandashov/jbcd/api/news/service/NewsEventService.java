package com.karandashov.jbcd.api.news.service;

import com.karandashov.jbcd.api.news.dto.request.CreateNewsEventRequest;
import com.karandashov.jbcd.api.news.dto.NewsEvent;
import com.karandashov.jbcd.api.news.dto.NewsEventList;
import com.karandashov.jbcd.api.news.entity.NewsEventEntity;
import com.karandashov.jbcd.api.news.exceptions.BusinessError;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.karandashov.jbcd.api.news.repository.NewsEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsEventService {

    private final NewsEventRepository repository;

    @Cacheable(value = "news", key = "#id")
    public NewsEvent getById(UUID id) {
        NewsEventEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(BusinessError.NEWS_NOT_FOUND));
        var response = map(entity);
        log.info("Success get news by id: {}", response);
        return response;
    }

    @Cacheable(value = "news_list")
    public NewsEventList getAll() {
        List<NewsEvent> events = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map(this::map)
                .toList();
        log.info("Success get all news: {}", events);
        return new NewsEventList(events);
    }

    @CacheEvict(value = {"news_list"}, allEntries = true)
    public NewsEvent save(CreateNewsEventRequest request) {
        NewsEventEntity entity = new NewsEventEntity(null, request.getTitle(), request.getMessage());
        entity = repository.save(entity);
        var response = map(entity);
        log.info("Success create news: {}", response);
        return response;
    }

    private NewsEvent map(NewsEventEntity entity) {
        return new NewsEvent(entity.getId(), entity.getTitle(), entity.getMessage());
    }
}
