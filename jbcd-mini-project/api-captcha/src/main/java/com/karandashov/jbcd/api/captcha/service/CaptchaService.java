package com.karandashov.jbcd.api.captcha.service;

import com.karandashov.jbcd.api.captcha.dto.request.SolveRequest;
import com.karandashov.jbcd.api.captcha.dto.response.CaptchaResponse;
import com.karandashov.jbcd.api.captcha.dto.response.SolveResponse;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository repository;

    public CheckCaptchaResponse checkAndExpireCaptcha() {
        // todo
    }

    public CaptchaResponse generateCaptcha() {
        // todo
    }


    public SolveResponse solve(UUID id, SolveRequest request) {
        // todo
    }
}
