package com.karandashov.dto;

import java.util.Collection;

/**
 * Контейнер для основной сущности, чтобы возвращать список объектов
 */
@SuppressWarnings("unused")
public class NewsEventList {

    private final Collection<NewsEvent> events;

    public NewsEventList(Collection<NewsEvent> events) {
        this.events = events;
    }

    public Collection<NewsEvent> getEvents() {
        return events;
    }
}
