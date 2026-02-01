package com.karandashov.repository;

import com.karandashov.exception.EntityNotFound;
import com.karandashov.model.Entity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Реализация in-memory хранения в виде строки
 */
public abstract class InMemoryEntityCrudRepository<T extends Entity> extends EntityCrudRepository<T> {

    /**
     * Хранение в виде простой HashMap (Hashtable - синхронизированная HashMap).
     * Не делайте так в реальности!
     * Лучше используйте {@link java.util.concurrent.ConcurrentHashMap} - будет эффективнее
     */
    protected final Map<String, String> map;

    public InMemoryEntityCrudRepository() {
        map = new Hashtable<>();
    }

    @Override
    public void create(T entity) {
        map.put(entity.getId(), serialize(entity));
    }

    @Override
    public void deleteById(String id) {
        map.remove(id);
    }

    @Override
    public List<T> readAll() {
        List<T> entityList = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            entityList.add(deserialize(entry.getValue()));
        }
        return entityList;
    }

    /**
     * Для работы этого типа репозитория обязательно требуется метод превращения сущности в строку
     */
    protected abstract String serialize(T entity);

    /**
     * Для работы этого типа репозитория обязательно требуется метод возрождения сущности из строки
     */
    protected abstract T deserialize(String serialized);

    /**
     * Переопределяю метод из {@link EntityCrudRepository} поскольку
     * в данной реализации его можно сделать эффективнее
     */
    @Override
    public T readById(String id) {
        String serialized = map.get(id);
        if (serialized == null) {
            throw new EntityNotFound(id);
        }
        return deserialize(serialized);
    }

    /**
     * Переопределяю метод из {@link EntityCrudRepository} поскольку
     * в данной реализации его можно сделать эффективнее
     */
    @Override
    public void update(T entity) {
        create(entity);
    }
}
