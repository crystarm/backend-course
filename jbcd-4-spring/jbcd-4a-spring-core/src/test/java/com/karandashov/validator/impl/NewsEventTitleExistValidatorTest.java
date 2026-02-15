package com.karandashov.validator.impl;

import com.karandashov.dto.NewsEvent;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsEventTitleExistValidatorTest {

    @Mock
    private NewsEventRepository repository;

    @InjectMocks
    private NewsEventTitleExistValidator validator;

    @Test
    void validate_WhenTitleIsUnique_ShouldNotThrowException() {
        String uniqueTitle = "Unique Title";
        NewsEvent existingEvent = new NewsEvent(UUID.randomUUID(), "Existing Title", "Some message");
        NewsEvent newEvent = new NewsEvent(UUID.randomUUID(), uniqueTitle, "Another message");

        when(repository.getAll()).thenReturn(List.of(existingEvent));

        assertDoesNotThrow(() -> validator.validate(newEvent));
    }

    @Test
    void validate_WhenTitleAlreadyExists_ShouldThrowException() {
        String duplicateTitle = "Duplicate Title";
        NewsEvent existingEvent = new NewsEvent(UUID.randomUUID(), duplicateTitle, "Some message");
        NewsEvent newEvent = new NewsEvent(UUID.randomUUID(), duplicateTitle, "Another message");

        when(repository.getAll()).thenReturn(List.of(existingEvent));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(newEvent));
        assertEquals("Title must be unique", exception.getMessage());
    }

    @Test
    void validate_WhenRepositoryIsEmpty_ShouldNotThrowException() {
        NewsEvent newEvent = new NewsEvent(UUID.randomUUID(), "Any Title", "Some message");

        when(repository.getAll()).thenReturn(List.of());

        assertDoesNotThrow(() -> validator.validate(newEvent));
    }
}
