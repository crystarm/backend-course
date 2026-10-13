package ru.karandashov.repository;

import ru.karandashov.entity.GameEntity;

import java.util.List;

public interface GameRepository {

    int save(GameEntity entity);

    GameEntity findById(int id);

    GameEntity findByField(String title);

    List<GameEntity> findAll();

    boolean update(GameEntity entity);

    void deleteById(int id);
}
