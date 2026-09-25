package com.karandashov.repository;

import com.karandashov.model.Document;
import com.karandashov.model.Snils;

import java.nio.file.Path;

public class SnilsJsonFileDocumentRepository extends JsonFileDocumentRepository {

    public SnilsJsonFileDocumentRepository(Path directory) {
        super(directory);
    }

    @Override
    protected boolean supports(Document document) {
        return document instanceof Snils;
    }
}
