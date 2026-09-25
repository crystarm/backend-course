package com.karandashov;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    // пример статической переменной
    private static int counter = 0;

    public static void main(String[] args) {
        // примитивные типы
        int number = 42;
        double decimal = 3.14;
        boolean flag = true;
        char symbol = 'A';

        // ссылочные типы
        String text = "Hello Java";
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);

        Map<String, Integer> scores = new HashMap<>();
        scores.put("Player1", 100);

        // примеры основных конструкций

        if (number > 40) {
            System.out.println("Number is greater than 40");
        }

        for (int i = 0; i < 3; i++) {
            System.out.println("Counter: " + i);
        }

        while (counter < 2) {
            System.out.println("While loop iteration");
            counter++;
        }

        // вызов метода
        int sum = add(5, 3);
        System.out.println("Sum is: " + sum);

        // перехват исключения
        try {
            int result = number / 0;
        } catch (ArithmeticException e) {
            System.out.println("Cannot divide by zero!");
        } catch (Exception e) {
            System.out.println("Unexpected exception!");
        } finally { // это необязательно
            System.out.println("Final action");
        }
    }

    // пример метода
    public static int add(int a, int b) {
        return a + b;
    }
}