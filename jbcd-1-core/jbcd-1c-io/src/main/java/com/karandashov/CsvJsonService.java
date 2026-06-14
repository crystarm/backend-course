package com.karandashov;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvJsonService {

    public List<User> readUsers(Path csvFile) throws IOException {
        List<User> users = new ArrayList<>();

        // BufferedReader читает файл построчно и не загружает весь файл в память сразу.
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            // Первая строка CSV содержит заголовки колонок, поэтому пропускаем ее.
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                users.add(new User(
                        Long.parseLong(columns[0]),
                        columns[1],
                        columns[2]
                ));
            }
        }

        return users;
    }

    public void writeUsers(Path jsonFile, List<User> users) throws IOException {
        // BufferedWriter буферизует запись: это быстрее, чем писать маленькими кусками напрямую.
        try (BufferedWriter writer = Files.newBufferedWriter(jsonFile, StandardCharsets.UTF_8)) {
            writer.write("[");
            writer.newLine();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                writer.write("  {\"id\": " + user.id()
                        + ", \"name\": \"" + user.name()
                        + "\", \"email\": \"" + user.email() + "\"}");
                if (i < users.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }
            writer.write("]");
        }
    }
}
