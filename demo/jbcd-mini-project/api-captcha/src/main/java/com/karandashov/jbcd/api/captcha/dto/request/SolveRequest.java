package com.karandashov.jbcd.api.captcha.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolveRequest {

    @NotBlank(message = "Captcha answer must not be blank")
    private String answer;
}
