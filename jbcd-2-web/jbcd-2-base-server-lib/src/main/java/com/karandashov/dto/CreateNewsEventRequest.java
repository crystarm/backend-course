package com.karandashov.dto;

/**
 * Запрос для создания сущности, содержит только необходимые поля
 */
@SuppressWarnings("unused")
public class CreateNewsEventRequest {

    private String title;

    private String message;

    public CreateNewsEventRequest() {
    }

    public CreateNewsEventRequest(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
