package com.karandashov.dao;

import com.karandashov.exception.EntityNotFound;
import com.karandashov.model.impl.Snils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SnilsDaoTest {

    private SnilsDao snilsDao;

    @BeforeEach
    void setUp() {
        snilsDao = new SnilsDao();
    }

    @Test
    void testCreateAndReadById() {
        // Создаем и сохраняем СНИЛС
        Snils snils = new Snils("123-456-789-00");
        snilsDao.create(snils);

        // Проверяем что можем получить по ID
        Snils retrieved = snilsDao.readById("123-456-789-00");
        assertEquals("123-456-789-00", retrieved.getNumber());
    }

    @Test
    void testReadAll() {
        // Создаем несколько СНИЛС
        Snils snils1 = new Snils("123-456-789-00");
        Snils snils2 = new Snils("987-654-321-00");
        snilsDao.create(snils1);
        snilsDao.create(snils2);

        // Получаем все и проверяем
        List<Snils> allSnils = snilsDao.readAll();
        assertEquals(2, allSnils.size());
        assertTrue(allSnils.stream().anyMatch(s -> s.getNumber().equals("123-456-789-00")));
        assertTrue(allSnils.stream().anyMatch(s -> s.getNumber().equals("987-654-321-00")));
    }

    @Test
    void testDeleteById() {
        // Создаем СНИЛС
        Snils snils = new Snils("123-456-789-00");
        snilsDao.create(snils);

        // Удаляем
        snilsDao.deleteById("123-456-789-00");

        // Проверяем что удален
        assertThrows(EntityNotFound.class, () -> snilsDao.readById("123-456-789-00"));
    }

    @Test
    void testSerializeDeserialize() {
        // Создаем СНИЛС
        Snils snils = new Snils("123-456-789-00");

        // Сериализуем
        String serialized = snilsDao.serialize(snils);
        assertEquals("123-456-789-00", serialized);

        // Десериализуем
        Snils deserialized = snilsDao.deserialize(serialized);
        assertEquals("123-456-789-00", deserialized.getNumber());
    }
}
