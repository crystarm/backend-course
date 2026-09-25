package ru.karandashov;

import ru.karandashov.repository.CsvCharacterRepository;
import ru.karandashov.writer.CharacterNameCsvWriter;

import java.io.InputStream;
import java.nio.file.Path;

public final class Main {

    private static final String CHARACTERS_RESOURCE = "characters.csv";
    private static final Path DEFAULT_OUTPUT_FILE = Path.of("result", "unique-character-names.csv");

    private Main() {
    }

    public static void main(String[] args) {
        Path outputFile = args.length == 0 ? DEFAULT_OUTPUT_FILE : Path.of(args[0]);

        var repository = new CsvCharacterRepository();
        var writer = new CharacterNameCsvWriter();
        var names = repository.readUniqueSortedNames(openCharactersResource());
        writer.write(outputFile, names);

        System.out.printf("Записано уникальных имён: %d. Файл: %s%n",
                names.size(), outputFile.toAbsolutePath());
    }

    private static InputStream openCharactersResource() {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(CHARACTERS_RESOURCE);
        if (inputStream == null) {
            throw new IllegalStateException("Ресурс не найден: " + CHARACTERS_RESOURCE);
        }
        return inputStream;
    }
}
