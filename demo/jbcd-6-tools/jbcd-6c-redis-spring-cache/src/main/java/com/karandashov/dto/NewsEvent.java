package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Основная сущность, с которой происходит работа. Превращается в json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsEvent implements Serializable {

    private UUID id;

    private String title;

    private String message;
}
