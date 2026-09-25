package com.karandashov.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * Контейнер для основной сущности, чтобы возвращать список объектов
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "News list dto")
public class NewsEventList {

    @ArraySchema(schema = @Schema(implementation = NewsEvent.class))
    private Collection<NewsEvent> events;
}
