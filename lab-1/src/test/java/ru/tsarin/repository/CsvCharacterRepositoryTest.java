package ru.tsarin.repository;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvCharacterRepositoryTest {

    private final CsvCharacterRepository repository = new CsvCharacterRepository();

    @Test
    void shouldReadUniqueNamesInAlphabeticalOrder() {
        String csv = """
                id,name,status,species,type,gender,origin/name,location/name,created
                2,Rick Sanchez,Alive,Human,,Male,Earth,Citadel,2017-11-04T18:48:46.250Z
                1,Morty Smith,Alive,Human,,Male,Earth,Citadel,2017-11-04T18:50:21.651Z
                3,Rick Sanchez,Alive,Human,,Male,Earth,Citadel,2017-11-04T18:55:00.000Z
                """;

        var names = repository.readUniqueSortedNames(inputStreamOf(csv));

        assertInstanceOf(TreeSet.class, names);
        assertEquals(List.of("Morty Smith", "Rick Sanchez"), names.stream().toList());
    }

    @Test
    void shouldRejectRowWithUnexpectedColumnCount() {
        String csv = """
                id,name,status,species,type,gender,origin/name,location/name,created
                1,Rick Sanchez,Alive
                """;

        var exception = assertThrows(IllegalArgumentException.class,
                () -> repository.readUniqueSortedNames(inputStreamOf(csv)));

        assertEquals("Некорректное количество столбцов в строке 2", exception.getMessage());
    }

    @Test
    void shouldRejectCsvWithoutNameColumn() {
        String csv = "id,title,status\n1,Test,Alive\n";

        var exception = assertThrows(IllegalArgumentException.class,
                () -> repository.readUniqueSortedNames(inputStreamOf(csv)));

        assertEquals("В CSV отсутствует обязательный столбец name", exception.getMessage());
    }

    private ByteArrayInputStream inputStreamOf(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
