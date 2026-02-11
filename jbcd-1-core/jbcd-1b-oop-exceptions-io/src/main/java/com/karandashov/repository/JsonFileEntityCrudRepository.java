package com.karandashov.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.karandashov.exception.FileException;
import com.karandashov.model.Entity;
import tools.jackson.databind.json.JsonMapper;

import java.io.*;
import java.util.List;

import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Реализация хранения в файле в виде json
 */
public abstract class JsonFileEntityCrudRepository<T extends Entity> extends EntityCrudRepository<T> {

    protected final String fileName;

    protected final JsonMapper jsonMapper;

    public JsonFileEntityCrudRepository(String fileName) {
        this.fileName = fileName;
        this.jsonMapper = JsonMapper.builder()
                // выключаем падения при неизвестных json-полях
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                // не включаем в json значения null (null в json является bad practice: ключ есть, а значения нет)
                .changeDefaultPropertyInclusion(include -> include.withValueInclusion(JsonInclude.Include.NON_NULL))
                .build();
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
