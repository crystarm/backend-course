package com.karandashov.repository;

import com.karandashov.model.Document;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class JsonFileDocumentRepository implements DocumentRepository {

    private final Path directory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected JsonFileDocumentRepository(Path directory) {
        this.directory = directory;
    }

    @Override
    public final void save(Document document) {
        // Это template method: общий алгоритм один для всех файловых репозиториев.
        // Конкретные классы выбирают только поддерживаемый тип документа.
        validateSupported(document);
        Document preparedDocument = prepare(document);
        Path file = resolveFile(preparedDocument);
        store(preparedDocument, file);
        afterSave(preparedDocument, file);
    }

    protected abstract boolean supports(Document document);

    protected Document prepare(Document document) {
        return document;
    }

    protected Path resolveFile(Document document) {
        // У файла предсказуемое имя: type-id.json. Так студент легко найдет результат.
        return directory.resolve(document.getType() + "-" + document.getId() + ".json");
    }

    protected void store(Document document, Path file) {
        try {
            // try/catch оставлен здесь, чтобы публичный save не заставлял main работать с IOException.
            Files.createDirectories(directory);
            objectMapper.writeValue(file.toFile(), document);
        } catch (IOException e) {
            throw new IllegalStateException("Can not save document to file", e);
        }
    }

    protected void afterSave(Document document, Path file) {
        System.out.println("Document saved to file " + file + ": " + document);
    }

    private void validateSupported(Document document) {
        if (!supports(document)) {
            throw new IllegalArgumentException("Repository does not support document type: " + document.getType());
        }
    }
}
