package com.karandashov.jbcd.api.captcha.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest {

    @Mock
    private CaptchaRepository repository;

    @Test
    void generateCreatesCaptchaInCreatedStatus() {
        CaptchaService service = createService();

        CaptchaResponse response = service.generate();

        ArgumentCaptor<CaptchaEntity> entityCaptor = ArgumentCaptor.forClass(CaptchaEntity.class);
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(repository).save(entityCaptor.capture(), ttlCaptor.capture());

        CaptchaEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getId()).isEqualTo(response.getId());
        assertThat(savedEntity.getStatus()).isEqualTo(CaptchaStatus.CREATED);
        assertThat(savedEntity.getAnswer()).isNotBlank();
        assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofMinutes(5));
        assertThat(response.getImageBase64()).startsWith("data:image/png;base64,");
    }

    @Test
    void solveMarksCaptchaAsSolvedWhenAnswerMatches() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        CaptchaEntity entity = new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED);
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        SolveResponse response = service.solve(id, new SolveRequest("AbC12"));

        assertThat(response).isEqualTo(new SolveResponse(true, "Captcha solved successfully"));
        ArgumentCaptor<CaptchaEntity> entityCaptor = ArgumentCaptor.forClass(CaptchaEntity.class);
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(repository).save(entityCaptor.capture(), ttlCaptor.capture());
        assertThat(entityCaptor.getValue().getStatus()).isEqualTo(CaptchaStatus.SOLVED);
        assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    void solveDeletesCaptchaWhenAnswerDoesNotMatch() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED)));

        SolveResponse response = service.solve(id, new SolveRequest("wrong"));

        assertThat(response).isEqualTo(new SolveResponse(false, "Captcha answer is incorrect"));
        verify(repository).deleteById(id);
    }

    @Test
    void solveThrowsWhenCaptchaWasNotFound() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.solve(id, new SolveRequest("abc12")))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError()).isEqualTo(BusinessError.CAPTCHA_NOT_FOUND));
    }

    @Test
    void checkAndExpireReturnsSuccessForSolvedCaptcha() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new CaptchaEntity(id, "abc12", CaptchaStatus.SOLVED)));

        CheckCaptchaResponse response = service.checkAndExpire(id);

        assertThat(response.isValid()).isTrue();
        verify(repository).deleteById(id);
    }

    @Test
    void checkAndExpireThrowsWhenCaptchaWasNotSolved() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.of(new CaptchaEntity(id, "abc12", CaptchaStatus.CREATED)));

        assertThatThrownBy(() -> service.checkAndExpire(id))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError()).isEqualTo(BusinessError.CAPTCHA_NOT_SOLVED));
    }

    @Test
    void checkAndExpireThrowsWhenCaptchaWasNotFound() {
        CaptchaService service = createService();

        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.checkAndExpire(id))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError()).isEqualTo(BusinessError.CAPTCHA_NOT_FOUND));
    }

    private CaptchaService createService() {
        return new CaptchaService(
                repository,
                new CaptchaTtlProperties(Duration.ofMinutes(5), Duration.ofSeconds(10))
        );
    }
}
