package com.karandashov.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karandashov.exception.FileException;
import com.karandashov.model.Entity;

import java.io.*;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Реализация хранения в файле в виде json
 */
public abstract class JsonFileEntityCrudRepository<T extends Entity> extends EntityCrudRepository<T> {

    protected final String fileName;

    protected final ObjectMapper jsonMapper;

    public JsonFileEntityCrudRepository(String fileName) {
        this.fileName = fileName;
        this.jsonMapper = new ObjectMapper();
        jsonMapper.findAndRegisterModules(); // обнаруживаем jsr310 для работы с datetime
        jsonMapper.configure(INCLUDE_SOURCE_IN_LOCATION, true);
        jsonMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        writeFile("[]"); // очищаем файл
    }

    @Override
    public List<T> readAll() {
        String json = readFileAsString();
        return deserialize(json);
    }

    @Override
    public void create(T entity) {
        List<T> entityList = readAll();
        for (T existedEntity : entityList) {
            if (existedEntity.getId().equals(entity.getId())) {
                update(entity); // обновим, если уже существует
                return;
            }
        }
        entityList.add(entity);
        String json = serialize(entityList);
        writeFile(json);
    }

    @Override
    public void deleteById(String id) {
        List<T> entityList = readAll();
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i).getId().equals(id)) {
                entityList.remove(i);
                break;
            }
        }
        String json = serialize(entityList);
        writeFile(json);
    }

    private String readFileAsString() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            throw new FileException(fileName, FileException.Action.READ, e);
        }
    }

    private void writeFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(content);
        } catch (IOException e) {
            throw new FileException(fileName, FileException.Action.WRITE, e);
        }
    }

    /**
     * Для работы этого типа репозитория обязательно требуется метод превращения сущностей в строку
     */
    protected abstract String serialize(List<T> entityList);

    /**
     * Для работы этого типа репозитория обязательно требуется метод возрождения сущности из строки
     */
    protected abstract List<T> deserialize(String json);
}
