package com.karandashov.dao;

import com.karandashov.model.impl.Snils;
import com.karandashov.repository.InMemoryEntityCrudRepository;

public class SnilsDao extends InMemoryEntityCrudRepository<Snils> {

    @Override
    protected String serialize(Snils entity) {
        return entity.getNumber();
    }

    @Override
    protected Snils deserialize(String serialized) {
        return new Snils(serialized);
    }
}
