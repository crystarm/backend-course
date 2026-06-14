package com.karandashov.repository;

import com.karandashov.model.Document;
import com.karandashov.model.Snils;

public class SnilsInMemoryDocumentRepository extends InMemoryDocumentRepository {

    @Override
    protected boolean supports(Document document) {
        return document instanceof Snils;
    }
}
