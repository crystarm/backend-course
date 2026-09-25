package ru.karandashov.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class CsvCharacterRepository {

    private static final String COLUMN_SEPARATOR = ",";
    private static final int NAME_COLUMN_INDEX = 1;

    public SortedSet<String> readUniqueSortedNames(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "Поток с CSV не должен быть null");

        try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            int columnCount = validateAndGetColumnCount(header);
            var names = new TreeSet<String>();

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isBlank()) {
                    names.add(readName(line, lineNumber, columnCount));
                }
            }
            return names;
        } catch (IOException exception) {
            throw new UncheckedIOException("Не удалось прочитать CSV с персонажами", exception);
        }
    }

    private int validateAndGetColumnCount(String header) {
        if (header == null) {
            throw new IllegalArgumentException("CSV-файл пуст");
        }

        String[] columns = header.split(COLUMN_SEPARATOR, -1);
        if (columns.length <= NAME_COLUMN_INDEX || !"name".equals(columns[NAME_COLUMN_INDEX])) {
            throw new IllegalArgumentException("В CSV отсутствует обязательный столбец name");
        }
        return columns.length;
    }

    private String readName(String line, int lineNumber, int columnCount) {
        String[] columns = line.split(COLUMN_SEPARATOR, -1);
        if (columns.length != columnCount) {
            throw new IllegalArgumentException("Некорректное количество столбцов в строке " + lineNumber);
        }

        String name = columns[NAME_COLUMN_INDEX].trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Пустое имя персонажа в строке " + lineNumber);
        }
        return name;
    }
}
