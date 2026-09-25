package com.karandashov;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для проверки функциональности класса Main
 */
public class MainTest {

    /**
     * Тест сложения положительных чисел
     */
    @Test
    public void testAddPositiveNumbers() {
        assertEquals(5, Main.add(2, 3), "2 + 3 должно равняться 5");
    }

    /**
     * Тест сложения с нулем
     */
    @Test
    public void testAddWithZero() {
        assertEquals(7, Main.add(7, 0), "7 + 0 должно равняться 7");
    }

    /**
     * Тест сложения отрицательных чисел
     */
    @Test
    public void testAddNegativeNumbers() {
        assertEquals(-5, Main.add(-2, -3), "-2 + (-3) должно равняться -5");
    }

    /**
     * Тест сложения положительного и отрицательного числа
     */
    @Test
    public void testAddPositiveAndNegativeNumbers() {
        assertEquals(-1, Main.add(2, -3), "2 + (-3) должно равняться -1");
    }
}