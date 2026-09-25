package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Репозиторий для хранения сущностей в памяти
 */
@Repository // в данном случае тоже самое что @Component, но с оттенком логики хранения
// однако при использовании базы данных — @Repository добавит немного логики обработки sql-ошибок
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

    public void clear() {
        STORAGE.clear();
    }
}
