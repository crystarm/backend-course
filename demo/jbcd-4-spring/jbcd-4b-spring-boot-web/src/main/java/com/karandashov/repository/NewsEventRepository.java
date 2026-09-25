package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsEventRepository {

    Optional<NewsEvent> getById(UUID id);

    List<NewsEvent> getAll();

    void save(NewsEvent newsEvent);
}
