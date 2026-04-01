package com.karandashov.controller;

import com.karandashov.dto.ApprovedRequest;
import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.service.NewsEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsEventController {

    private final NewsEventService service;

    /**
     * Запускаем workflow публикации новости.
     */
    @PostMapping
    public void create(@RequestBody CreateNewsEventRequest request) {
        log.info("Request to create news: {}", request);
        service.create(request);
    }

    /**
     * Читаем текущее состояние workflow по id новости.
     */
    @GetMapping("/{id}")
    public NewsEvent getById(@PathVariable UUID id) {
        log.info("Request to get news by id: {}", id);
        return service.getById(id);
    }

    /**
     * Отправляем signal в уже запущенный workflow.
     */
    @PostMapping("/status")
    public void process(@RequestBody ApprovedRequest request) {
        log.info("Request for approve/decline news: {}", request);
        service.process(request);
    }
}
