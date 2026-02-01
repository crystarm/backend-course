package com.karandashov.exception;

public class EntityNotFound extends RuntimeException {

    public EntityNotFound(String id) {
        super("Не найдена сущность с id = " + id);
    }
}
