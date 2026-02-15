package com.karandashov.dto;

import lombok.Getter;
import lombok.ToString;

/**
 * Универсальное тело ответа. Структура подходит и для успешного ответа и для ошибки
 */
@SuppressWarnings("unused")
@Getter
@ToString
public class UniversalResponse<T> {

    private final int code;

    private final String message;

    private final T data;

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
