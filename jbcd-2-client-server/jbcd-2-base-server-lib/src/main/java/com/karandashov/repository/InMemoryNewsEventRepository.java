package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий для хранения сущностей в памяти
 */
public class InMemoryNewsEventRepository implements NewsEventRepository {

    private final ConcurrentHashMap<UUID, NewsEvent> STORAGE;

    public InMemoryNewsEventRepository() {
        this.STORAGE = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<NewsEvent> getById(UUID id) {
        return Optional.ofNullable(STORAGE.get(id));
    }

    @Override
    public List<NewsEvent> getAll() {
        return List.copyOf(STORAGE.values());
    }

    @Override
    public void save(NewsEvent newsEvent) {
        STORAGE.put(newsEvent.getId(), newsEvent);
    }
}
