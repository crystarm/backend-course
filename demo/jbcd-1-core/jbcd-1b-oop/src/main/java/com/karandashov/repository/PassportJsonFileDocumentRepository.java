package com.karandashov.repository;

import com.karandashov.model.Document;
import com.karandashov.model.Passport;

import java.nio.file.Path;

public class PassportJsonFileDocumentRepository extends JsonFileDocumentRepository {

    public PassportJsonFileDocumentRepository(Path directory) {
        super(directory);
    }

    @Override
    protected boolean supports(Document document) {
        return document instanceof Passport;
    }
}
