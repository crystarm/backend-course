package com.karandashov.parser;

import com.karandashov.model.User;
import com.karandashov.parser.impl.HashSetCsvParser;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HashSetCsvParserTest {

    @Test
    void parse_returnsSetWithUniqueUsers() {
        HashSetCsvParser parser = new HashSetCsvParser("test-file.csv");
        Set<User> users = parser.parseAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getId() == 0));
        assertTrue(users.stream().anyMatch(u -> u.getId() == 1));
    }
}
