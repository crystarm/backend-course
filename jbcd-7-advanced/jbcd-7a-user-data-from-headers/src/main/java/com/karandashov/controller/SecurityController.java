package com.karandashov.controller;

import com.karandashov.dto.UniversalResponse;
import com.karandashov.dto.UserInfoHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SecurityController {

    @GetMapping("/me")
    public ResponseEntity<UniversalResponse<UserInfoHeaders>> currentUser(UserInfoHeaders userInfoHeaders) {
        return ResponseEntity.ok(new UniversalResponse<>(userInfoHeaders));
    }
}
