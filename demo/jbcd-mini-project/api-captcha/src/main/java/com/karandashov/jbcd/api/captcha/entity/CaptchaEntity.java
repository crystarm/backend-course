package com.karandashov.jbcd.api.captcha.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaEntity {

    private UUID id;

    private String answer;

    private CaptchaStatus status;
}
