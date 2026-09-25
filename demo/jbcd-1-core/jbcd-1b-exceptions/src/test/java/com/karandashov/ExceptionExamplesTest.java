package com.karandashov;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExceptionExamplesTest {

    private final ExceptionExamples examples = new ExceptionExamples();

    @Test
    void shouldThrowCheckedException() {
        assertThrows(Exception.class, examples::throwBaseCheckedException);
    }

    @Test
    void shouldThrowUncheckedException() {
        assertThrows(RuntimeException.class, examples::throwBaseUncheckedException);
    }

    @Test
    void shouldCatchCustomCheckedException() {
        assertEquals("checked: Document file can not be read", examples.catchCheckedException());
    }

    @Test
    void shouldCatchCustomUncheckedException() {
        assertEquals("unchecked: Document number is invalid", examples.catchUncheckedException());
    }
}
