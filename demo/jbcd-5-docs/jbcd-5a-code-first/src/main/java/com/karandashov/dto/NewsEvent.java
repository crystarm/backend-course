package com.karandashov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Основная сущность, с которой происходит работа. Превращается в json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Main news dto")
public class NewsEvent {

    @Schema(description = "News ID", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    private UUID id;

    @Schema(description = "News title", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[\\s\\S]{1,100}$")
    private String title;

    @Schema(description = "News description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;
}
