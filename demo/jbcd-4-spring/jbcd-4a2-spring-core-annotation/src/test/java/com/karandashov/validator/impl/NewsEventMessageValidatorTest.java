package com.karandashov.validator.impl;

import com.karandashov.dto.NewsEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NewsEventMessageValidatorTest {

    private final NewsEventMessageValidator validator = new NewsEventMessageValidator();

    @Test
    void validate_WhenMessageIsValid_ShouldNotThrowException() {
        NewsEvent event = new NewsEvent(UUID.randomUUID(), "Title", "Valid message length");
        assertDoesNotThrow(() -> validator.validate(event));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Short", "Too short"})
    void validate_WhenMessageIsTooShort_ShouldThrowException(String shortMessage) {
        NewsEvent event = new NewsEvent(UUID.randomUUID(), "Title", shortMessage);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(event));
        assertEquals("Message length must be more than 10 symbols", exception.getMessage());
    }

    @Test
    void validate_WhenMessageIsTooLong_ShouldThrowException() {
        String longMessage = "a".repeat(1001);
        NewsEvent event = new NewsEvent(UUID.randomUUID(), "Title", longMessage);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(event));
        assertEquals("Message length must be less than 1000 symbols", exception.getMessage());
    }

    @Test
    void validate_WhenMessageIsExactlyAtLowerBound_ShouldThrowException() {
        // event.getMessage().length() < 10
        String nineChars = "123456789";
        NewsEvent event = new NewsEvent(UUID.randomUUID(), "Title", nineChars);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> validator.validate(event));
        assertEquals("Message length must be more than 10 symbols", exception.getMessage());
    }

    @Test
    void validate_WhenMessageIsExactlyTenChars_ShouldNotThrowException() {
        String tenChars = "1234567890";
        NewsEvent event = new NewsEvent(UUID.randomUUID(), "Title", tenChars);
        assertDoesNotThrow(() -> validator.validate(event));
    }
}
