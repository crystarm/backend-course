package com.karandashov.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create news event request")
public class CreateNewsEventRequest {

    @Size(min = 1, max = 100)
    @NotBlank(message = "Заголовок не может быть пустым")
    @Schema(description = "News title", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[\\s\\S]{1,100}$")
    private String title;

    @NotBlank(message = "Сообщение не может быть пустым")
    @Schema(description = "News description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String message;
}
