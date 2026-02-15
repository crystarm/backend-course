package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Основная сущность, с которой происходит работа. Превращается в json
 */
@Data
@AllArgsConstructor
public class NewsEvent {

    private final UUID id;

    private final String title;

    private final String message;
}
