package com.karandashov.controller;

import com.karandashov.news.api.NewsApi;
import com.karandashov.news.dto.CreateNewsEventRequest;
import com.karandashov.news.dto.NewsEvent;
import com.karandashov.news.dto.NewsEventList;
import com.karandashov.service.NewsEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NewsEventController implements NewsApi {

    private final NewsEventService service;

    @Override
    public ResponseEntity<NewsEventList> getAll() {
        var response = service.getAll();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<NewsEvent> getById(UUID id) {
        var response = service.getById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<NewsEvent> save(CreateNewsEventRequest createNewsEventRequest) {
        var response = service.save(createNewsEventRequest);
        return ResponseEntity.ok(response);
    }
}
