package com.karandashov.jbcd.api.captcha.service;

import com.karandashov.jbcd.api.captcha.configuration.properties.CaptchaTtlProperties;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.karandashov.jbcd.api.captcha.dto.request.SolveRequest;
import com.karandashov.jbcd.api.captcha.dto.response.CaptchaResponse;
import com.karandashov.jbcd.api.captcha.dto.response.SolveResponse;
import com.karandashov.jbcd.api.captcha.entity.CaptchaEntity;
import com.karandashov.jbcd.api.captcha.entity.CaptchaStatus;
import com.karandashov.jbcd.api.captcha.exceptions.BusinessError;
import com.karandashov.jbcd.api.captcha.repository.CaptchaRepository;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository repository;
    private final CaptchaTtlProperties ttlProperties;

    public CheckCaptchaResponse checkAndExpire(UUID id) {
        CaptchaEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(BusinessError.CAPTCHA_NOT_FOUND));

        if (entity.getStatus() != CaptchaStatus.SOLVED) {
            throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED);
        }

        repository.deleteById(id);
        return new CheckCaptchaResponse(true);
    }

    public CaptchaResponse generate() {
        UUID id = UUID.randomUUID();
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        String answer = captcha.text().trim().toLowerCase();

        CaptchaEntity entity = new CaptchaEntity(id, answer, CaptchaStatus.CREATED);
        repository.save(entity, ttlProperties.created());

        return new CaptchaResponse(id, captcha.toBase64());
    }

    public SolveResponse solve(UUID id, SolveRequest request) {
        CaptchaEntity entity = repository.findById(id)
                .orElseThrow(() -> new BaseException(BusinessError.CAPTCHA_NOT_FOUND));

        String normalizedAnswer = request.getAnswer().trim().toLowerCase();
        if (!entity.getAnswer().equals(normalizedAnswer)) {
            repository.deleteById(id);
            return new SolveResponse(false, "Captcha answer is incorrect");
        }

        entity.setStatus(CaptchaStatus.SOLVED);
        repository.save(entity, ttlProperties.solved());
        return new SolveResponse(true, "Captcha solved successfully");
    }
}
