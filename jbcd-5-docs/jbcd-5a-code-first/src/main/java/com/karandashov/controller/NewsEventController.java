package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.service.NewsEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
@Tag(name = "News", description = "News operations")
public class NewsEventController {

    private final NewsEventService service;

    @GetMapping
    @Operation(summary = "Get all news")
    @ApiResponse(responseCode = "200", description = "List of news", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = UniversalResponse.class)
    ))
    public UniversalResponse<NewsEventList> getAll() {
        log.info("Request to get all news");
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get news by id")
    @ApiResponse(responseCode = "200", description = "News found", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = UniversalResponse.class)
    ))
    @ApiResponse(responseCode = "404", description = "News not found", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = UniversalResponse.class)
    ))
    public UniversalResponse<NewsEvent> getById(@PathVariable UUID id) {
        log.info("Request to get news by id: {}", id);
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "Create news")
    @ApiResponse(responseCode = "200", description = "News created", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = UniversalResponse.class)
    ))
    public UniversalResponse<NewsEvent> save(@Valid @RequestBody CreateNewsEventRequest request) {
        log.info("Request to save news: {}", request);
        return service.save(request);
    }
}
