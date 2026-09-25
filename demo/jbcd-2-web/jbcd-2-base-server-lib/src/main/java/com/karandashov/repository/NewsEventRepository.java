package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Контракт для работы с новостями. Есть уже готовая простая InMemoryNewsEventRepository реализация,
 * но в основном приложении можно реализовать свою собственную реализацию, например, на базе БД.
 */
public interface NewsEventRepository {

    Optional<NewsEvent> getById(UUID id);

    List<NewsEvent> getAll();

    void save(NewsEvent newsEvent);
}
