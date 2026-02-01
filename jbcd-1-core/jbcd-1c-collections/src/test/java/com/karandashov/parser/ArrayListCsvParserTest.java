package com.karandashov.parser;

import com.karandashov.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListCsvParserTest {

    @Test
    void parse_returnsArrayListWithAllUsers() {
        ArrayListCsvParser parser = new ArrayListCsvParser("test-file.csv");
        List<User> users = parser.parse();

        assertEquals(2, users.size(), "Должно быть считано 2 пользователя без заголовка");
        assertEquals(0, users.get(0).getId());
        assertEquals(1, users.get(1).getId());
        assertEquals("Тестбург", users.get(0).getLocation());
        assertNull(users.get(1).getLocation(), "Пустое значение в CSV должно преобразовываться в null");
    }
}
