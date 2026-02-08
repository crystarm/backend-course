package com.karandashov.parser;

import com.karandashov.model.User;
import com.karandashov.parser.impl.TreeMapCsvParser;
import org.junit.jupiter.api.Test;

import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.*;

class TreeMapCsvParserTest {

    @Test
    void parse_returnsSortedMapById() {
        TreeMapCsvParser parser = new TreeMapCsvParser("test-file.csv");
        SortedMap<Integer, User> map = parser.parseAll();

        assertEquals(2, map.size());
        assertEquals(0, map.firstKey());
        assertEquals(1, map.lastKey());
        assertEquals(0, map.get(0).getId());
        assertEquals(1, map.get(1).getId());
    }
}
