package com.karandashov.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewsEventRequest {

    @Size(min = 1, max = 100)
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;

    @NotBlank(message = "Сообщение не может быть пустым")
    private String message;
}
