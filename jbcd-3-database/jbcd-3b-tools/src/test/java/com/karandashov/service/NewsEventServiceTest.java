package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NewsEventServiceTest {

    private NewsEventRepository repository;

    private NewsEventService service;

    @BeforeEach
    void setUp() {
        // Create a new repository instance for each test
        repository = mock(NewsEventRepository.class);
        service = new NewsEventService(repository);
    }

    @Test
    void testGetById_success() {
        // Arrange
        UUID id = UUID.randomUUID();
        NewsEvent event = new NewsEvent(id, "Title", "Message");
        when(repository.getById(id)).thenReturn(Optional.of(event));

        // Act
        UniversalResponse<NewsEvent> response = service.getById(id);

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(id, response.getData().getId());
        assertEquals("Title", response.getData().getTitle());
        assertEquals("Message", response.getData().getMessage());
        verify(repository, times(1)).getById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetById_notFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.getById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getById(id));
        verify(repository, times(1)).getById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testGetAll_success() {
        // Arrange
        NewsEvent e1 = new NewsEvent(UUID.randomUUID(), "T1", "M1");
        NewsEvent e2 = new NewsEvent(UUID.randomUUID(), "T2", "M2");
        when(repository.getAll()).thenReturn(List.of(e1, e2));

        // Act
        UniversalResponse<NewsEventList> response = service.getAll();

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getEvents());
        assertEquals(2, response.getData().getEvents().size());
        assertTrue(response.getData().getEvents().stream().anyMatch(e -> e.getId().equals(e1.getId())));
        assertTrue(response.getData().getEvents().stream().anyMatch(e -> e.getId().equals(e2.getId())));
        verify(repository, times(1)).getAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testSave_success() {
        // Arrange
        CreateNewsEventRequest request = new CreateNewsEventRequest("New Title", "New Message");
        ArgumentCaptor<NewsEvent> captor = ArgumentCaptor.forClass(NewsEvent.class);
        doNothing().when(repository).save(captor.capture());

        // Act
        UniversalResponse<NewsEvent> response = service.save(request);

        // Assert
        assertEquals(0, response.getCode());
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
        assertNotNull(response.getData().getId());
        assertEquals("New Title", response.getData().getTitle());
        assertEquals("New Message", response.getData().getMessage());

        // The same instance created inside service should be passed to repo and returned in response
        NewsEvent saved = captor.getValue();
        assertNotNull(saved);
        assertSame(saved, response.getData());
        verify(repository, times(1)).save(any(NewsEvent.class));
        verifyNoMoreInteractions(repository);
    }
}