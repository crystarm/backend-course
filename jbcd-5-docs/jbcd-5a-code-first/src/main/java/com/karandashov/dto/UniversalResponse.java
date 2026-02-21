package com.karandashov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Универсальное тело ответа. Структура подходит и для успешного ответа и для ошибки
 */
@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Universal response dto")
public class UniversalResponse<T> {

    @Schema(description = "Business error code", requiredMode = Schema.RequiredMode.REQUIRED)
    private int code;

    @Schema(description = "Message", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;

    private T data;

    public UniversalResponse(T data) {
        this.code = 0;
        this.message = "SUCCESS";
        this.data = data;
    }

    public UniversalResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }
}
