package com.karandashov.jbcd.client.captcha.client;

import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@HttpExchange(accept = "application/json")
public interface CaptchaClient {

    @PostExchange("/internal/captcha/{id}/checkAndExpire")
    CheckCaptchaResponse checkAndExpire(@PathVariable("id") UUID id);
}
