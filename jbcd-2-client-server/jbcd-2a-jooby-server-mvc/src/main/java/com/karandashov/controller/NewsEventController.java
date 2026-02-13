package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.service.NewsEventService;
import io.jooby.annotation.GET;
import io.jooby.annotation.POST;
import io.jooby.annotation.Path;
import io.jooby.annotation.PathParam;

import java.util.UUID;

@Path("/")
public class NewsEventController {

    private final NewsEventService service;

    public NewsEventController(NewsEventService service) {
        this.service = service;
    }

    @GET
    public UniversalResponse<NewsEventList> getAll() {
        return service.getAll();
    }

    @GET("/{id}")
    public UniversalResponse<NewsEvent> getById(@PathParam String id) {
        return service.getById(UUID.fromString(id));
    }

    @POST
    public UniversalResponse<NewsEvent> save(CreateNewsEventRequest request) {
        return service.save(request);
    }
}
