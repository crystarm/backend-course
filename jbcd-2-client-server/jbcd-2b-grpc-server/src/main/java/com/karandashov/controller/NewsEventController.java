package com.karandashov.controller;

import com.karandashov.grpc.*;
import com.karandashov.service.NewsEventService;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

/**
 * Контроллер для обработки gRPC запросов к сервису новостных событий.
 * Наследуется от сгенерированного класса NewsEventServiceGrpc.NewsEventServiceImplBase
 * и реализует методы для обработки запросов.
 */
public class NewsEventController extends NewsEventServiceGrpc.NewsEventServiceImplBase {

    /**
     * Сервис для работы с новостными событиями
     */
    private final NewsEventService service;

    /**
     * Конструктор контроллера
     *
     * @param service сервис для работы с новостными событиями
     */
    public NewsEventController(NewsEventService service) {
        this.service = service;
    }

    /**
     * Получение всех новостных событий
     *
     * @param request          пустой запрос
     * @param responseObserver обработчик ответа
     */
    @Override
    public void getAllNewsEvents(Empty request, StreamObserver<UniversalResponse> responseObserver) {
        // Получаем все новости из сервиса
        com.karandashov.dto.UniversalResponse<com.karandashov.dto.NewsEventList> serviceResponse = service.getAll();

        // Преобразуем DTO в gRPC объекты
        NewsEventList.Builder newsEventListBuilder = NewsEventList.newBuilder();

        // Добавляем все новости в список
        serviceResponse.getData().getEvents().forEach(event -> newsEventListBuilder.addEvents(convertDtoToGrpc(event)));

        // Создаем ответ
        UniversalResponse response = UniversalResponse.newBuilder()
                .setCode(serviceResponse.getCode())
                .setMessage(serviceResponse.getMessage())
                .setNewsEventList(newsEventListBuilder.build())
                .build();

        // Отправляем ответ
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Получение новостного события по ID
     *
     * @param request          запрос с ID новости
     * @param responseObserver обработчик ответа
     */
    @Override
    public void getNewsEvent(GetNewsEventRequest request, StreamObserver<UniversalResponse> responseObserver) {
        try {
            // Преобразуем строковый ID в UUID
            UUID id = UUID.fromString(request.getId());

            // Получаем новость из сервиса
            com.karandashov.dto.UniversalResponse<com.karandashov.dto.NewsEvent> serviceResponse = service.getById(id);

            // Преобразуем DTO в gRPC объект
            NewsEvent grpcNewsEvent = convertDtoToGrpc(serviceResponse.getData());

            // Создаем ответ
            UniversalResponse response = UniversalResponse.newBuilder()
                    .setCode(serviceResponse.getCode())
                    .setMessage(serviceResponse.getMessage())
                    .setNewsEvent(grpcNewsEvent)
                    .build();

            // Отправляем ответ
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // В случае ошибки отправляем ответ с кодом ошибки
            UniversalResponse response = UniversalResponse.newBuilder()
                    .setCode(404)
                    .setMessage("Ошибка при получении новости: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * Создание нового новостного события
     *
     * @param request          запрос с данными новости
     * @param responseObserver обработчик ответа
     */
    @Override
    public void createNewsEvent(CreateNewsEventRequest request, StreamObserver<UniversalResponse> responseObserver) {
        try {
            // Преобразуем gRPC запрос в DTO
            com.karandashov.dto.CreateNewsEventRequest dtoRequest = new com.karandashov.dto.CreateNewsEventRequest(
                    request.getTitle(),
                    request.getMessage()
            );

            // Сохраняем новость через сервис
            com.karandashov.dto.UniversalResponse<com.karandashov.dto.NewsEvent> serviceResponse = service.save(dtoRequest);

            // Преобразуем DTO в gRPC объект
            NewsEvent grpcNewsEvent = convertDtoToGrpc(serviceResponse.getData());

            // Создаем ответ
            UniversalResponse response = UniversalResponse.newBuilder()
                    .setCode(serviceResponse.getCode())
                    .setMessage(serviceResponse.getMessage())
                    .setNewsEvent(grpcNewsEvent)
                    .build();

            // Отправляем ответ
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            // В случае ошибки отправляем ответ с кодом ошибки
            UniversalResponse response = UniversalResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Ошибка при создании новости: " + e.getMessage())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * Преобразование DTO объекта NewsEvent в gRPC объект NewsEvent
     *
     * @param dtoEvent DTO объект
     * @return gRPC объект
     */
    private NewsEvent convertDtoToGrpc(com.karandashov.dto.NewsEvent dtoEvent) {
        return NewsEvent.newBuilder()
                .setId(dtoEvent.getId().toString())
                .setTitle(dtoEvent.getTitle())
                .setMessage(dtoEvent.getMessage())
                .build();
    }
}
