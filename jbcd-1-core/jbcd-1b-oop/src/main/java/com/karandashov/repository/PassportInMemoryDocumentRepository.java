package com.karandashov.repository;

import com.karandashov.model.Document;
import com.karandashov.model.Passport;

public class PassportInMemoryDocumentRepository extends InMemoryDocumentRepository {

    @Override
    protected boolean supports(Document document) {
        return document instanceof Passport;
    }
}
