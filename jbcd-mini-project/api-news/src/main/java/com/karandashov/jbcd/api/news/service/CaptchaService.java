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
            CheckCaptchaResponse response = captchaClient.checkAndExpire(solvedCaptchaId);
            if (!response.isValid()) {
                throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED);
            }
        } catch (CaptchaClientException exception) {
            Integer errorCode = exception.getCode();
            if (Integer.valueOf(2000).equals(errorCode)) {
                throw new BaseException(BusinessError.CAPTCHA_NOT_FOUND_OR_EXPIRED, exception);
            }
            if (Integer.valueOf(2001).equals(errorCode)) {
                throw new BaseException(BusinessError.CAPTCHA_NOT_SOLVED, exception);
            }
            throw new BaseException(BusinessError.CAPTCHA_CHECK_FAILED, exception);
        } catch (RestClientException exception) {
            throw new BaseException(BusinessError.CAPTCHA_CHECK_FAILED, exception);
        }
    }
}
