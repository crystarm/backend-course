package com.karandashov.dao;

import com.karandashov.exception.EntityNotFound;
import com.karandashov.model.impl.Passport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PassportDaoTest {

    private PassportDao passportDao;
    private String testFilePath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Создаем временный файл для тестов
        testFilePath = tempDir.resolve("test-passports.json").toString();
        passportDao = new PassportDao(testFilePath);
    }

    @AfterEach
    void tearDown() {
        // Удаляем тестовый файл
        File file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testCreateAndReadById() {
        // Создаем и сохраняем паспорт
        Passport passport = new Passport(1234, 567890, "Иванов Иван Иванович", LocalDate.of(1990, 1, 1));
        passportDao.create(passport);

        // Проверяем что можем получить по ID
        Passport retrieved = passportDao.readById("1234-567890");
        assertEquals(1234, retrieved.getSeries());
        assertEquals(567890, retrieved.getNumber());
        assertEquals("Иванов Иван Иванович", retrieved.getFullName());
        assertEquals(LocalDate.of(1990, 1, 1), retrieved.getBirthDate());
    }

    @Test
    void testReadAll() {
        // Создаем несколько паспортов
        Passport passport1 = new Passport(1234, 567890, "Иванов Иван Иванович", LocalDate.of(1990, 1, 1));
        Passport passport2 = new Passport(5678, 123456, "Петров Петр Петрович", LocalDate.of(1985, 5, 5));
        passportDao.create(passport1);
        passportDao.create(passport2);

        // Получаем все и проверяем
        List<Passport> allPassports = passportDao.readAll();
        assertEquals(2, allPassports.size());
        assertTrue(allPassports.stream().anyMatch(p -> p.getId().equals("1234-567890")));
        assertTrue(allPassports.stream().anyMatch(p -> p.getId().equals("5678-123456")));
    }

    @Test
    void testUpdate() {
        // Создаем паспорт
        Passport passport = new Passport(1234, 567890, "Иванов Иван Иванович", LocalDate.of(1990, 1, 1));
        passportDao.create(passport);

        // Обновляем на новый с тем же ID, но другими данными
        Passport updatedPassport = new Passport(1234, 567890, "Иванов Иван Петрович", LocalDate.of(1990, 1, 1));
        passportDao.update(updatedPassport);

        // Проверяем что обновление прошло успешно
        Passport retrieved = passportDao.readById("1234-567890");
        assertEquals("Иванов Иван Петрович", retrieved.getFullName());
    }

    @Test
    void testDeleteById() {
        // Создаем паспорт
        Passport passport = new Passport(1234, 567890, "Иванов Иван Иванович", LocalDate.of(1990, 1, 1));
        passportDao.create(passport);

        // Удаляем
        passportDao.deleteById("1234-567890");

        // Проверяем что удален
        assertThrows(EntityNotFound.class, () -> passportDao.readById("1234-567890"));
    }

    @Test
    void testSerializeDeserialize() {
        // Создаем паспорт
        Passport passport = new Passport(1234, 567890, "Иванов Иван Иванович", LocalDate.of(1990, 1, 1));
        passportDao.create(passport);

        // Получаем все паспорта
        List<Passport> passports = passportDao.readAll();
        assertEquals(1, passports.size());

        // Проверяем что данные сохранились корректно
        Passport retrieved = passports.getFirst();
        assertEquals(1234, retrieved.getSeries());
        assertEquals(567890, retrieved.getNumber());
        assertEquals("Иванов Иван Иванович", retrieved.getFullName());
        assertEquals(LocalDate.of(1990, 1, 1), retrieved.getBirthDate());
    }
}
