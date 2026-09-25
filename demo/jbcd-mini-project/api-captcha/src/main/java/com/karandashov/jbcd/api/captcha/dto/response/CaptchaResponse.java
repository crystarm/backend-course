package com.karandashov.jbcd.api.captcha.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {

    private UUID id;

    private String imageBase64;
}
