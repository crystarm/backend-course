package ru.karandashov.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacterNameCsvWriterTest {

    private final CharacterNameCsvWriter writer = new CharacterNameCsvWriter();

    @Test
    void shouldWriteHeaderAndNames(@TempDir Path temporaryDirectory) throws IOException {
        Path outputFile = temporaryDirectory.resolve("nested/names.csv");
        var names = new TreeSet<>(List.of("Morty Smith", "Rick Sanchez"));

        writer.write(outputFile, names);

        assertEquals(List.of("name", "Morty Smith", "Rick Sanchez"), Files.readAllLines(outputFile));
    }
}
