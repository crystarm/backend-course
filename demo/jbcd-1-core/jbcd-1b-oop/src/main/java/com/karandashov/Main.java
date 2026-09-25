package com.karandashov;

import com.karandashov.model.Document;
import com.karandashov.model.Passport;
import com.karandashov.model.Snils;
import com.karandashov.repository.DocumentRepository;
import com.karandashov.repository.PassportInMemoryDocumentRepository;
import com.karandashov.repository.PassportJsonFileDocumentRepository;
import com.karandashov.repository.SnilsInMemoryDocumentRepository;
import com.karandashov.repository.SnilsJsonFileDocumentRepository;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Document snils = new Snils("1", "123-456-789 00");
        Document passport = new Passport("2", "1234", "567890");

        // Папка создастся автоматически при первом сохранении файловым репозиторием.
        Path outputDirectory = Path.of("documents");

        // Один и тот же бизнес-код работает с разными типами документов и хранилищ.
        saveDocument(snils, new SnilsInMemoryDocumentRepository());
        saveDocument(snils, new SnilsJsonFileDocumentRepository(outputDirectory));
        saveDocument(passport, new PassportInMemoryDocumentRepository());
        saveDocument(passport, new PassportJsonFileDocumentRepository(outputDirectory));
    }

    public static void saveDocument(Document document, DocumentRepository repository) {
        // Полиморфизм: Main не знает, куда именно сохраняется документ.
        repository.save(document);
    }
}
