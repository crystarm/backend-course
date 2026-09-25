package com.karandashov.jbcd.api.captcha.controller;

import com.karandashov.jbcd.api.captcha.BaseTest;
import com.karandashov.jbcd.api.captcha.dto.request.SolveRequest;
import com.karandashov.jbcd.api.captcha.entity.CaptchaEntity;
import com.karandashov.jbcd.api.captcha.entity.CaptchaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class CaptchaApiTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generateReturnsCaptchaIdAndImage() throws Exception {
        String response = mockMvc.perform(post("/captcha/generate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.imageBase64").value(org.hamcrest.Matchers.startsWith("data:image/png;base64,")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(jsonMapper.readTree(response).get("id").asString());
        CaptchaEntity entity = findCaptcha(id).orElseThrow();

        assertThat(entity.getStatus()).isEqualTo(CaptchaStatus.CREATED);
        assertThat(entity.getAnswer()).isNotBlank();
        assertThat(getCaptchaTtlSeconds(id)).isBetween(1L, Duration.ofMinutes(5).getSeconds());
    }

    @Test
    void solveReturnsSuccessForCorrectAnswer() throws Exception {
        UUID id = UUID.randomUUID();
        saveCaptcha(new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED), Duration.ofMinutes(5));

        mockMvc.perform(post("/captcha/{id}/solve", id)
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new SolveRequest("AbC12"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(true))
                .andExpect(jsonPath("$.message").value("Captcha solved successfully"));

        CaptchaEntity entity = findCaptcha(id).orElseThrow();
        assertThat(entity.getStatus()).isEqualTo(CaptchaStatus.SOLVED);
        assertThat(getCaptchaTtlSeconds(id)).isBetween(1L, 10L);
    }

    @Test
    void solveDeletesCaptchaForWrongAnswer() throws Exception {
        UUID id = UUID.randomUUID();
        saveCaptcha(new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED), Duration.ofMinutes(5));

        mockMvc.perform(post("/captcha/{id}/solve", id)
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new SolveRequest("wrong"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solved").value(false))
                .andExpect(jsonPath("$.message").value("Captcha answer is incorrect"));

        assertThat(findCaptcha(id)).isEmpty();
    }

    @Test
    void solveReturns404WhenCaptchaWasNotFound() throws Exception {
        mockMvc.perform(post("/captcha/{id}/solve", UUID.randomUUID())
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(new SolveRequest("abc12"))))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("captcha.not.found.or.expired"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void solveReturns400ForBlankAnswer() throws Exception {
        mockMvc.perform(post("/captcha/{id}/solve", UUID.randomUUID())
                .contentType(APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(new SolveRequest(" "))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad.request"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Validation error")));
    }

    @Test
    void internalCheckAndExpireReturnsSuccessForSolvedCaptcha() throws Exception {
        UUID id = UUID.randomUUID();
        saveCaptcha(new CaptchaEntity(id, "abc12", CaptchaStatus.SOLVED), Duration.ofSeconds(10));

        mockMvc.perform(post("/internal/captcha/{id}/checkAndExpire", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        assertThat(findCaptcha(id)).isEmpty();
    }

    @Test
    void internalCheckAndExpireReturns400ForUnsolvedCaptcha() throws Exception {
        UUID id = UUID.randomUUID();
        saveCaptcha(new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED), Duration.ofMinutes(5));

        mockMvc.perform(post("/internal/captcha/{id}/checkAndExpire", id))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("captcha.not.solved"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void internalCheckAndExpireReturns404WhenCaptchaWasNotFound() throws Exception {
        mockMvc.perform(post("/internal/captcha/{id}/checkAndExpire", UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("captcha.not.found.or.expired"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()));
    }
}
