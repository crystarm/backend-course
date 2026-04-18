package com.karandashov.dto.request;

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

    @Size(min = 1, max = 4000)
    @NotBlank(message = "Сообщение не может быть пустым")
    private String message;
}
