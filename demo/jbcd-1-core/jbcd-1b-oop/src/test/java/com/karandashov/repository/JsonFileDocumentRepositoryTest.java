package com.karandashov.repository;

import com.karandashov.Main;
import com.karandashov.model.Passport;
import com.karandashov.model.Snils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonFileDocumentRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSavePassportToJsonFile() throws IOException {
        Passport document = new Passport("42", "1234", "567890");
        PassportJsonFileDocumentRepository repository = new PassportJsonFileDocumentRepository(tempDir);

        Main.saveDocument(document, repository);

        Path file = tempDir.resolve("passport-42.json");
        String json = Files.readString(file);
        assertTrue(json.contains("\"id\":\"42\""));
        assertTrue(json.contains("\"series\":\"1234\""));
        assertTrue(json.contains("\"number\":\"567890\""));
    }

    @Test
    void shouldSaveSnilsToJsonFile() {
        Snils document = new Snils("7", "123-456-789 00");
        SnilsJsonFileDocumentRepository repository = new SnilsJsonFileDocumentRepository(tempDir);

        Main.saveDocument(document, repository);

        assertTrue(Files.exists(tempDir.resolve("snils-7.json")));
    }
}
