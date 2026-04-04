package com.karandashov.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class SecurityController {

    // todo /me получить информацию о пользователе

    // todo /registration простая регистрация нового пользователя с добавлением в бд (пароль обязательно хэширован!!! через spring security encoder)
}
