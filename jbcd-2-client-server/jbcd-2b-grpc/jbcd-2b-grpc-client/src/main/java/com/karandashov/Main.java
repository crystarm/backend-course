package com.karandashov;

// используются сгенерированные классы

import com.google.common.util.concurrent.ListenableFuture;
import com.karandashov.grpc.CreateNewsEventRequest;
import com.karandashov.grpc.Empty;
import com.karandashov.grpc.NewsEventServiceGrpc;
import com.karandashov.grpc.UniversalResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Главный класс gRPC клиента для тестирования производительности
 */
public class Main {

    /**
     * Количество запросов для тестирования
     */
    private static final int COUNT = 1000;

    /**
     * Хост-сервера
     */
    private static final String HOST = "localhost";

    /**
     * Порт сервера
     */
    private static final int PORT = 8080;

    private static NewsEventServiceGrpc.NewsEventServiceFutureStub futureStub;

    /**
     * Точка входа в приложение
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Создаем канал для подключения к серверу
        // Без SSL/TLS

        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .usePlaintext() // Без SSL/TLS
                .build();

        // Создаем стаб для вызова методов сервера
        futureStub = NewsEventServiceGrpc.newFutureStub(channel);
        
        // Хранилище ответов
        List<ListenableFuture<UniversalResponse>> responses = new ArrayList<>(COUNT*3);

        long start = System.currentTimeMillis();
        System.out.println("Начало: " + start);

        for (int i = 0; i < COUNT; i++) {
            responses.add(create());
            responses.add(getAll());
        }

        // Wait for all responses
        for (ListenableFuture<UniversalResponse> response : responses) {
            try {
                response.get();
            } catch (Exception e) {
                System.err.println("Error while waiting for response: " + e.getMessage());
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Конец: " + end);

        double avg = (end - start) / 2.0 / COUNT;

        System.out.println("Среднее время: " + avg);

        // Закрываем канал при завершении работы
        try {
            if (channel != null) {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            System.err.println("Ошибка при закрытии канала: " + e.getMessage());
        }

    }

    /**
     * Отправка запроса на создание новости
     */
    private static ListenableFuture<UniversalResponse> create() {
        try {
            // Создаем запрос на создание новости
            CreateNewsEventRequest request = CreateNewsEventRequest.newBuilder()
                    .setTitle("Тестовая новость")
                    .setMessage("Это тестовая новость, созданная gRPC клиентом")
                    .build();

            // Отправляем запрос и получаем ответ
            return futureStub.createNewsEvent(request);
        } catch (Exception e) {
            System.err.println("Ошибка при вызове метода createNewsEvent: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Отправка запроса на получение всех новостей
     */
    private static ListenableFuture<UniversalResponse> getAll() {
        try {
            Empty request = Empty.newBuilder().build();
            return futureStub.getAllNewsEvents(request);
        } catch (Exception e) {
            System.err.println("Ошибка при вызове метода getAllNewsEvents: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
