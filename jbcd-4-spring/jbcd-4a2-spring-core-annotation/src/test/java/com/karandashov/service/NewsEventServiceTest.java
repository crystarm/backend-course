package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class NewsEventServiceTest {

    private InMemoryNewsEventRepository repository;
    private NewsEventService service;

    @BeforeEach
    void setUp() {
        // Пересоздаем каждый раз для чистоты и независимости каждого отдельного теста
        repository = new InMemoryNewsEventRepository();
        service = new NewsEventService(repository, new ArrayList<>());
    }

    @Test
    void testGetByIdWithExistingId() {
        // Arrange
        UUID id = UUID.randomUUID();
        NewsEvent newsEvent = new NewsEvent(id, "Test Title", "Test Message");
        repository.save(newsEvent);

        // Act
        UniversalResponse<NewsEvent> response = service.getById(id);

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(id, response.getData().getId());
        assertEquals("Test Title", response.getData().getTitle());
        assertEquals("Test Message", response.getData().getMessage());
    }

    @Test
    void testGetByIdWithNonExistingId() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getById(id));
    }

    @Test
    void testGetAll() {
        // Arrange
        NewsEvent newsEvent1 = new NewsEvent(UUID.randomUUID(), "Title 1", "Message 1");
        NewsEvent newsEvent2 = new NewsEvent(UUID.randomUUID(), "Title 2", "Message 2");
        repository.save(newsEvent1);
        repository.save(newsEvent2);

        // Act
        UniversalResponse<NewsEventList> response = service.getAll();

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());

        Collection<NewsEvent> events = response.getData().getEvents();
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(newsEvent1.getId())));
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(newsEvent2.getId())));
    }

    @Test
    void testGetAllWithEmptyRepository() {
        // Act
        UniversalResponse<NewsEventList> response = service.getAll();

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getEvents().size());
    }

    @Test
    void testSave() {
        // Arrange
        CreateNewsEventRequest request = new CreateNewsEventRequest("New Title", "New Message");

        // Act
        UniversalResponse<NewsEvent> response = service.save(request);

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals("New Title", response.getData().getTitle());
        assertEquals("New Message", response.getData().getMessage());

        // Verify the event was actually saved in the repository
        UUID savedId = response.getData().getId();
        assertTrue(repository.getById(savedId).isPresent());
    }
}