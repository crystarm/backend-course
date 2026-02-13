package com.karandashov;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    private static final int COUNT = 1000;

    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final String BASE_URL = "http://" + HOST + ":" + PORT + "/";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        System.out.println("Начало: " + start);

        for (int i = 0; i < COUNT; i++) {
            create();
            getAll();
        }

        long end = System.currentTimeMillis();
        System.out.println("Конец: " + end);

        double avg = (end - start) / 2.0 / COUNT;

        System.out.println("Среднее время: " + avg);
    }

    private static void create() {
        String requestBody = """
                {
                  "title": "Test Title",
                  "message": "Test Message"
                }
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            // Отправляем запрос и получаем ответ
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Проверяем статус ответа
            assert response.statusCode() == 201;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void getAll() {

        HttpRequest getAllRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        try {
            // Отправляем запрос на получение всех новостей
            HttpResponse<String> response = httpClient.send(getAllRequest, HttpResponse.BodyHandlers.ofString());
            // Проверяем статус ответа
            assert 200 == response.statusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
