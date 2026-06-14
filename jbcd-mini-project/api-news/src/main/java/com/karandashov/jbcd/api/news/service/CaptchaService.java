package com.karandashov.jbcd.api.news.service;

import com.karandashov.jbcd.api.news.exceptions.BusinessError;
import com.karandashov.jbcd.client.captcha.client.CaptchaClient;
import com.karandashov.jbcd.client.captcha.client.CaptchaClientException;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaClient captchaClient;

    public void checkSolvedCaptcha(UUID solvedCaptchaId) {
        try {
            log.info("Send checkSolvedCaptcha request by id={}", solvedCaptchaId);
            CheckCaptchaResponse response = captchaClient.checkAndExpire(solvedCaptchaId);
            log.info("Get response: {}", response);
            if (!response.isValid()) {
                log.warn("Captcha was not solved");
                throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED);
            }
        } catch (CaptchaClientException exception) {
            String errorCode = exception.getCode();
            log.warn("Error code: {}", errorCode);
            if ("captcha.not.found.or.expired".equals(errorCode)) {
                throw new BaseException(BusinessError.CAPTCHA_NOT_FOUND_OR_EXPIRED, exception);
            }
            if ("captcha.not.solved".equals(errorCode)) {
                throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED, exception);
            }
            throw new BaseException(BusinessError.CAPTCHA_CHECK_FAILED, exception);
        } catch (RestClientException exception) {
            throw new BaseException(BusinessError.CAPTCHA_CHECK_FAILED, exception);
        }
    }
}
