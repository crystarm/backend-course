package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles(value = "test")
public class NewsEventServiceTest {

    @Autowired
    private InMemoryNewsEventRepository repository;

    @Autowired
    private NewsEventService service;

    @BeforeEach
    void setUp() {
        repository.clear();
    }

    @Test
    void testGetByIdWithExistingId() {
        // Arrange
        UUID id = UUID.randomUUID();
        NewsEvent newsEvent = new NewsEvent(id, "Test Title", "Test Message");
        repository.save(newsEvent);

        // Act
        NewsEvent response = service.getById(id);

        // Assert
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Message", response.getMessage());
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
        NewsEventList response = service.getAll();

        // Assert
        assertNotNull(response);

        Collection<NewsEvent> events = response.getEvents();
        assertEquals(2, events.size());
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(newsEvent1.getId())));
        assertTrue(events.stream().anyMatch(e -> e.getId().equals(newsEvent2.getId())));
    }

    @Test
    void testGetAllWithEmptyRepository() {
        // Act
        NewsEventList response = service.getAll();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getEvents().size());
    }

    @Test
    void testSave() {
        // Arrange
        CreateNewsEventRequest request = new CreateNewsEventRequest("New Title", "New Message");

        // Act
        NewsEvent response = service.save(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("New Title", response.getTitle());
        assertEquals("New Message", response.getMessage());

        // Verify the event was actually saved in the repository
        UUID savedId = response.getId();
        assertTrue(repository.getById(savedId).isPresent());
    }
}