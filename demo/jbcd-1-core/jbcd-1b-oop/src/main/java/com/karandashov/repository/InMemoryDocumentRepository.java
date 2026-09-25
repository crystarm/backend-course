package com.karandashov.repository;

import com.karandashov.model.Document;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class InMemoryDocumentRepository implements DocumentRepository {

    private final Map<String, Document> storage = new LinkedHashMap<>();

    @Override
    public final void save(Document document) {
        // Метод final: наследники не меняют общий сценарий сохранения целиком.
        // Они подстраивают только отдельные шаги через protected-методы ниже.
        validateSupported(document);
        Document preparedDocument = prepare(document);
        store(preparedDocument);
        afterSave(preparedDocument);
    }

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    protected abstract boolean supports(Document document);

    protected Document prepare(Document document) {
        // Шаг расширения: конкретный репозиторий может нормализовать документ перед сохранением.
        return document;
    }

    protected void store(Document document) {
        // Базовая реализация знает, как именно хранить документ в памяти.
        storage.put(document.getId(), document);
    }

    protected void afterSave(Document document) {
        System.out.println("Document saved in memory: " + document);
    }

    private void validateSupported(Document document) {
        if (!supports(document)) {
            throw new IllegalArgumentException("Repository does not support document type: " + document.getType());
        }
    }
}
