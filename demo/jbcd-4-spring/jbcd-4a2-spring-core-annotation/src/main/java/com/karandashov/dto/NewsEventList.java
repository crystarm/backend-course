package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

/**
 * Контейнер для основной сущности, чтобы возвращать список объектов
 */
@Data
@AllArgsConstructor
public class NewsEventList {

    private final Collection<NewsEvent> events;
}
