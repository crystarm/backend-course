package com.karandashov.parser;

import com.karandashov.model.User;
import com.karandashov.parser.impl.LinkedListCsvParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListCsvParserTest {

    @Test
    void parse_returnsLinkedListWithAllUsersInOrder() {
        LinkedListCsvParser parser = new LinkedListCsvParser("test-file.csv");
        List<User> users = parser.parseAll();

        assertEquals(2, users.size());
        assertEquals(0, users.get(0).getId());
        assertEquals(1, users.get(1).getId());
    }
}
