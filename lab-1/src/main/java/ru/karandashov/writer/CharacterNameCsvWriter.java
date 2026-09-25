package ru.karandashov.writer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.SortedSet;

public class CharacterNameCsvWriter {

    private static final String HEADER = "name";

    public void write(Path outputFile, SortedSet<String> names) {
        Objects.requireNonNull(outputFile, "Путь к выходному файлу не должен быть null");
        Objects.requireNonNull(names, "Множество имён не должно быть null");

        try {
            Path absoluteOutputFile = outputFile.toAbsolutePath();
            Files.createDirectories(absoluteOutputFile.getParent());

            try (var writer = Files.newBufferedWriter(absoluteOutputFile, StandardCharsets.UTF_8)) {
                writer.write(HEADER);
                writer.newLine();
                for (String name : names) {
                    writer.write(name);
                    writer.newLine();
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось записать результат в файл: " + outputFile, exception);
        }
    }
}
