package com.karandashov;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvJsonServiceTest {

    private final CsvJsonService service = new CsvJsonService();

    @TempDir
    Path tempDir;

    @Test
    void shouldReadUsersFromCsv() throws IOException, URISyntaxException {
        Path csvFile = Path.of(Objects.requireNonNull(getClass().getResource("/test-users.csv")).toURI());

        List<User> users = service.readUsers(csvFile);

        assertEquals(List.of(
                new User(1, "Ivan", "ivan@example.com"),
                new User(2, "Anna", "anna@example.com")
        ), users);
    }

    @Test
    void shouldWriteUsersToJson() throws IOException {
        Path jsonFile = tempDir.resolve("users.json");

        service.writeUsers(jsonFile, List.of(new User(1, "Ivan", "ivan@example.com")));

        String json = Files.readString(jsonFile);
        assertTrue(json.contains("\"id\": 1"));
        assertTrue(json.contains("\"name\": \"Ivan\""));
        assertTrue(json.contains("\"email\": \"ivan@example.com\""));
    }
}
