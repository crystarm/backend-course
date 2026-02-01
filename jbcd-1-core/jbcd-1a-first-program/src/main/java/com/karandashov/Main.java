package com.karandashov;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello and welcome!");

        // Пример использования метода сложения
        System.out.println("2 + 3 = " + add(2, 3));
    }

    /**
     * Метод для сложения двух чисел
     * 
     * @param a первое слагаемое
     * @param b второе слагаемое
     * @return сумма двух чисел
     */
    public static int add(int a, int b) {
        return a + b;
    }
}
