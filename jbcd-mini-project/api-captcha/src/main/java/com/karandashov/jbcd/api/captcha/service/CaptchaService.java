package com.karandashov.jbcd.api.captcha.service;

import com.karandashov.jbcd.api.captcha.configuration.MetricsConfiguration;
import com.karandashov.jbcd.api.captcha.configuration.properties.CaptchaTtlProperties;
import com.karandashov.jbcd.api.captcha.dto.request.SolveRequest;
import com.karandashov.jbcd.api.captcha.dto.response.CaptchaResponse;
import com.karandashov.jbcd.api.captcha.dto.response.SolveResponse;
import com.karandashov.jbcd.api.captcha.entity.CaptchaEntity;
import com.karandashov.jbcd.api.captcha.entity.CaptchaStatus;
import com.karandashov.jbcd.api.captcha.exceptions.BusinessError;
import com.karandashov.jbcd.api.captcha.repository.CaptchaRepository;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository repository;
    private final CaptchaTtlProperties ttlProperties;
    private final MetricService metricService;

    public CheckCaptchaResponse checkAndExpire(UUID id) {
        CaptchaEntity entity = repository.findById(id).orElseThrow(() -> {
            log.warn("Captcha {} not found", id);
            return new BaseException(BusinessError.CAPTCHA_NOT_FOUND);
        });

        if (entity.getStatus() != CaptchaStatus.SOLVED) {
            log.warn("Captcha {} is not solved", id);
            throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED);
        }

        repository.deleteById(id);
        metricService.incrementCaptchaCounter(MetricsConfiguration.CaptchaEvent.SUCCESS_EXPIRE);
        log.info("Captcha {} success check and has been expired", id);
        return new CheckCaptchaResponse(true);
    }

    public CaptchaResponse generate() {
        UUID id = UUID.randomUUID();
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 5);
        captcha.setCharType(Captcha.TYPE_DEFAULT);
        String answer = captcha.text().trim().toLowerCase();

        CaptchaEntity entity = new CaptchaEntity(id, answer, CaptchaStatus.CREATED);
        repository.save(entity, ttlProperties.created());
        metricService.incrementCaptchaCounter(MetricsConfiguration.CaptchaEvent.CREATED);

        log.info("Captcha {} created", id);
        return new CaptchaResponse(id, captcha.toBase64());
    }

    public SolveResponse solve(UUID id, SolveRequest request) {
        CaptchaEntity entity = repository.findById(id).orElseThrow(() -> {
            log.warn("Captcha {} not found by id ", id);
            return new BaseException(BusinessError.CAPTCHA_NOT_FOUND);
        });

        String normalizedAnswer = request.getAnswer().trim().toLowerCase();
        if (!entity.getAnswer().equals(normalizedAnswer)) {
            log.warn("Captcha {} answer is incorrect", id);
            repository.deleteById(id);
            metricService.incrementCaptchaCounter(MetricsConfiguration.CaptchaEvent.MISMATCH);
            return new SolveResponse(false, "Captcha answer is incorrect");
        }

        entity.setStatus(CaptchaStatus.SOLVED);
        repository.save(entity, ttlProperties.solved());
        metricService.incrementCaptchaCounter(MetricsConfiguration.CaptchaEvent.SOLVED);
        log.info("Captcha {} solved", id);
        return new SolveResponse(true, "Captcha solved successfully");
    }
}
