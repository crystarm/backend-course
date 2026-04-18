package com.karandashov.jbcd.api.captcha.controller;

import com.karandashov.jbcd.api.captcha.dto.request.SolveRequest;
import com.karandashov.jbcd.api.captcha.dto.response.CaptchaResponse;
import com.karandashov.jbcd.api.captcha.dto.response.SolveResponse;
import com.karandashov.jbcd.api.captcha.service.CaptchaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
public class CaptchaController {

    private final CaptchaService service;

    @PostMapping
    public CaptchaResponse generateCaptcha() {
        log.info("Request new captcha");
        return service.generateCaptcha();
    }

    @PostMapping("/{id}")
    public SolveResponse solve(
            @Valid @NotNull @PathVariable UUID id,
            @Valid @RequestBody SolveRequest request
    ) {
        log.info("Request to solve captcha by id: {}", id);
        return service.solve(id, request);
    }
}
