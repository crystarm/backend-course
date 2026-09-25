package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * Контейнер для основной сущности, чтобы возвращать список объектов
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsEventList implements Serializable {

    private Collection<NewsEvent> events;
}
