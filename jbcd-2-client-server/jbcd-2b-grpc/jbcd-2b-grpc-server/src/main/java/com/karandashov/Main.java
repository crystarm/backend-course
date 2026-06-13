package com.karandashov;

// используются сгенерированнные классы

import com.karandashov.controller.NewsEventController;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Главный класс приложения, запускающий gRPC сервер
 */
public class Main {

    /**
     * Порт, на котором будет запущен сервер
     */
    private static final int PORT = 8080;

    /**
     * Точка входа в приложение
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        try {
            // Создаем репозиторий для хранения новостных событий
            NewsEventRepository repository = new InMemoryNewsEventRepository();

            // Создаем сервис для работы с новостными событиями
            NewsEventService service = new NewsEventService(repository);

            // Создаем контроллер для обработки gRPC запросов
            NewsEventController controller = new NewsEventController(service);

            // Создаем и запускаем gRPC сервер
            Server server = ServerBuilder.forPort(PORT)
                    .addService(controller)
                    .build()
                    .start();

            System.out.println("gRPC сервер запущен на порту " + PORT);

            // Регистрируем обработчик для корректного завершения сервера при остановке приложения
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Останавливаем gRPC сервер...");
                server.shutdown();
                System.out.println("gRPC сервер остановлен");
            }));

            // Блокируем поток до завершения сервера
            server.awaitTermination();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Сервер прерван: " + e.getMessage());
        }
    }
}
