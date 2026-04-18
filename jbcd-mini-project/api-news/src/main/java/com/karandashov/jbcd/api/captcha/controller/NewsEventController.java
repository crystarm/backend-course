package com.karandashov.jbcd.api.captcha.controller;

import com.karandashov.jbcd.api.captcha.dto.request.CreateNewsEventRequest;
import com.karandashov.jbcd.api.captcha.dto.NewsEvent;
import com.karandashov.jbcd.api.captcha.dto.NewsEventList;
import com.karandashov.jbcd.api.captcha.service.NewsEventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping
    public NewsEventList getAll() {
        log.info("Request to get all news");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NewsEvent getById(@Valid @NotNull @PathVariable UUID id) {
        log.info("Request to get news by id: {}", id);
        return service.getById(id);
    }

    @PostMapping
    public NewsEvent save(@Valid @RequestBody CreateNewsEventRequest request) {
        log.info("Request to save news: {}", request);
        return service.save(request);
    }
}
