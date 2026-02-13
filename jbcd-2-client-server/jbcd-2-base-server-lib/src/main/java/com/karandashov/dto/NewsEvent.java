package com.karandashov.dto;

import java.util.UUID;

/**
 * Основная сущность, с которой происходит работа. Превращается в json
 */
@SuppressWarnings("unused")
public class NewsEvent {

    private final UUID id;

    private final String title;

    private final String message;

    public NewsEvent(UUID id, String title, String message) {
        this.id = id;
        this.title = title;
        this.message = message;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
