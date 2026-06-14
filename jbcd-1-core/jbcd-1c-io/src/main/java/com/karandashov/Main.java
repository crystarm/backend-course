package com.karandashov;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Path csvFile = Path.of(Objects.requireNonNull(Main.class.getResource("/users.csv")).toURI());
        Path jsonFile = Path.of("users.json");

        CsvJsonService service = new CsvJsonService();
        // Сначала превращаем строки CSV в обычные Java-объекты.
        List<User> users = service.readUsers(csvFile);
        // Затем записываем эти объекты в JSON-файл.
        service.writeUsers(jsonFile, users);

        System.out.println("Users were written to " + jsonFile.toAbsolutePath());
    }
}
