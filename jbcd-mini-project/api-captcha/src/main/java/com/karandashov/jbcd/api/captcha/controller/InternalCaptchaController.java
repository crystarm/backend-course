package com.karandashov.jbcd.api.captcha.controller;

import com.karandashov.jbcd.api.captcha.service.CaptchaService;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/captcha")
public class InternalCaptchaController {

    private final CaptchaService service;

    @PostMapping("/{id}/checkAndExpire")
    public CheckCaptchaResponse checkAndExpire(@Valid @NotNull @PathVariable UUID id) {
        log.info("Request check captcha {}", id);
        return service.checkAndExpire(id);
    }
}
