package com.karandashov.repository;

import com.karandashov.dto.NewsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryNewsEventRepositoryTest {

    private InMemoryNewsEventRepository repository;

    @BeforeEach
    void setUp() {
        // Пересоздаем каждый раз для чистоты и независимости каждого отдельного теста
        repository = new InMemoryNewsEventRepository();
    }

    @Test
    void testSaveAndGetById() {
        // Arrange
        UUID id = UUID.randomUUID();
        NewsEvent newsEvent = new NewsEvent(id, "Title", "Message");

        // Act
        repository.save(newsEvent);
        Optional<NewsEvent> result = repository.getById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(newsEvent, result.get());
        assertEquals(id, result.get().getId());
        assertEquals("Title", result.get().getTitle());
        assertEquals("Message", result.get().getMessage());
    }

    @Test
    void testGetByIdNonExisting() {
        // Act
        Optional<NewsEvent> result = repository.getById(UUID.randomUUID());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAll() {
        // Arrange
        NewsEvent newsEvent1 = new NewsEvent(UUID.randomUUID(), "Title 1", "Message 1");
        NewsEvent newsEvent2 = new NewsEvent(UUID.randomUUID(), "Title 2", "Message 2");
        repository.save(newsEvent1);
        repository.save(newsEvent2);

        // Act
        List<NewsEvent> result = repository.getAll();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(newsEvent1));
        assertTrue(result.contains(newsEvent2));
    }

    @Test
    void testGetAllEmpty() {
        // Act
        List<NewsEvent> result = repository.getAll();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveOverwrite() {
        // Arrange
        UUID id = UUID.randomUUID();
        NewsEvent newsEvent1 = new NewsEvent(id, "Title 1", "Message 1");
        NewsEvent newsEvent2 = new NewsEvent(id, "Title 2", "Message 2");

        // Act
        repository.save(newsEvent1);
        repository.save(newsEvent2);
        Optional<NewsEvent> result = repository.getById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Title 2", result.get().getTitle());
        assertEquals("Message 2", result.get().getMessage());
        assertEquals(1, repository.getAll().size());
    }
}
