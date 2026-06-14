package com.karandashov.repository;

import com.karandashov.Main;
import com.karandashov.model.Passport;
import com.karandashov.model.Snils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryDocumentRepositoryTest {

    @Test
    void shouldSaveSnilsToInMemoryRepository() {
        Snils document = new Snils("1", "123-456-789 00");
        SnilsInMemoryDocumentRepository repository = new SnilsInMemoryDocumentRepository();

        Main.saveDocument(document, repository);

        assertEquals(Optional.of(document), repository.findById("1"));
    }

    @Test
    void shouldRejectUnsupportedDocumentType() {
        SnilsInMemoryDocumentRepository repository = new SnilsInMemoryDocumentRepository();

        assertThrows(IllegalArgumentException.class, () -> repository.save(new Passport("1", "1234", "567890")));
    }
}
