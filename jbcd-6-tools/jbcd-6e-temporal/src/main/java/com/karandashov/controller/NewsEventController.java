package com.karandashov.controller;

import com.karandashov.dto.ApprovedRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
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
     * Метод для создания новости и запуска процесса согласования через Temporal
     */
    @PostMapping
    public void create(@RequestBody NewsEvent event) {
        log.info("Request to create news: {}", event);

    }

    /**
     * Метод для получения из Temporal новостей на шаге "на подтверждении"
     */
    @GetMapping("/unapproved")
    public NewsEventList getUnapprovedNews() {
        log.info("Request to get all unapproved news");

    }

    /**
     * Метод для подтверждения/отклонения новости
     */
    @PostMapping("/status")
    public NewsEvent process(@RequestBody ApprovedRequest request) {
        log.info("Request for approve/decline news: {}", request);

    }

    /**
     * Метод для получения всех согласованных новостей
     */
    @GetMapping
    public NewsEventList getNews() {
        log.info("Request to get all approved news");

    }
}
