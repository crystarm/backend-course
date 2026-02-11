package com.karandashov.dao;

import com.karandashov.model.impl.Passport;
import com.karandashov.repository.JsonFileEntityCrudRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class PassportDao extends JsonFileEntityCrudRepository<Passport> {

    public PassportDao(String fileName) {
        super(fileName);
    }

    @Override
    protected List<Passport> deserialize(String json) {
        try {
            return jsonMapper.readValue(json, new TypeReference<ArrayList<Passport>>() {});
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String serialize(List<Passport> entityList) {
        try {
            return jsonMapper.writeValueAsString(entityList);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
