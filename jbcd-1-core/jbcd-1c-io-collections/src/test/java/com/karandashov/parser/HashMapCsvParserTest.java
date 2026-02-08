package com.karandashov.parser;

import com.karandashov.model.User;
import com.karandashov.parser.impl.HashMapCsvParser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HashMapCsvParserTest {

    @Test
    void parse_returnsMapWithUsersById() {
        HashMapCsvParser parser = new HashMapCsvParser("test-file.csv");
        Map<Integer, User> users = parser.parseAll();

        assertEquals(2, users.size());
        assertTrue(users.containsKey(0));
        assertTrue(users.containsKey(1));

        assertEquals("Тестбург", users.get(0).getLocation());
        assertNull(users.get(1).getLocation());
    }
}
