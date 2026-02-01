package com.karandashov.repository;

import java.util.List;

/**
 * Контракт, описывающий, какими методами должен обладать репозиторий.
 * Репозиторий - это некоторое хранилище данных.
 * Репозиторий работает с некоторой сущностью entity класса T.
 * CRUD - create, read, update, delete
 */
public interface CrudRepository<T> {

    void create(T entity);

    T readById(String id);

    List<T> readAll();

    void update(T entity);

    void deleteById(String id);
}
