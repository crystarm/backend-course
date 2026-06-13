package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.service.NewsEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsEventController {

    private final NewsEventService service;

    @GetMapping
    public NewsEventList getAll() {
        log.info("Request to get all news");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NewsEvent getById(@PathVariable UUID id) {
        log.info("Request to get news by id: {}", id);
        return service.getById(id);
    }

    @PostMapping
    public NewsEvent save(@Valid @RequestBody CreateNewsEventRequest request) {
        log.info("Request to save news: {}", request);
        return service.save(request);
    }
}
