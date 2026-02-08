package com.karandashov.parser;

import com.karandashov.model.User;
import com.karandashov.parser.impl.PriorityQueueCsvParser;
import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueCsvParserTest {

    @Test
    void parse_returnsPriorityQueueOrderedById() {
        PriorityQueueCsvParser parser = new PriorityQueueCsvParser("test-file.csv");
        PriorityQueue<User> pq = parser.parseAll();

        assertEquals(2, pq.size());
        assertEquals(0, pq.peek().getId());
        assertEquals(0, pq.poll().getId());
        assertEquals(1, pq.poll().getId());
        assertTrue(pq.isEmpty());
    }
}
