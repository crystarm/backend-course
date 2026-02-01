package com.karandashov.repository;

import com.karandashov.exception.EntityNotFound;
import com.karandashov.model.Entity;

import java.util.List;

/**
 * Примитивная абстрактная реализация некоторого хранилища сущностей.
 * Реализует некоторые общие методы, но оставляет хранение за реализацией
 */
public abstract class EntityCrudRepository<T extends Entity> implements CrudRepository<T> {

    /**
     * Очень наивная реализация для примера - не делай так
     */
    @Override
    public T readById(String id) {
        List<T> entityList = readAll();
        for (T entity : entityList) {
            if (entity.getId().equals(id)) {
                return entity;
            }
        }
        throw new EntityNotFound(id);
    }

    /**
     * Очень наивная реализация для примера - не делай так
     */
    @Override
    public void update(T entity) {
        deleteById(entity.getId());
        create(entity);
    }
}
