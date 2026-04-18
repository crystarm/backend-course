package com.karandashov.jbcd.api.news.service;

import com.karandashov.jbcd.client.captcha.client.CaptchaClient;
import com.karandashov.jbcd.client.captcha.client.CaptchaClientException;
import com.karandashov.jbcd.client.captcha.response.CheckCaptchaResponse;
import com.karandashov.jbcd.api.news.dto.NewsEvent;
import com.karandashov.jbcd.api.news.dto.NewsEventList;
import com.karandashov.jbcd.api.news.dto.request.CreateNewsEventRequest;
import com.karandashov.jbcd.api.news.entity.NewsEventEntity;
import com.karandashov.jbcd.api.news.exceptions.BusinessError;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.karandashov.jbcd.api.news.repository.NewsEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsEventServiceTest {

    @Mock
    private NewsEventRepository repository;

    @Mock
    private CaptchaClient captchaClient;

    @Test
    void getByIdReturnsMappedNewsEvent() {
        NewsEventService service = createService();
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        NewsEvent result = service.getById(id);

        assertThat(result).isEqualTo(new NewsEvent(id, "Title", "Message"));
        verify(repository).findById(id);
    }

    @Test
    void getByIdThrowsBusinessExceptionWhenNewsWasNotFound() {
        NewsEventService service = createService();
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> {
                    BaseException baseException = (BaseException) exception;
                    assertThat(baseException.getError()).isEqualTo(BusinessError.NEWS_NOT_FOUND);
                });

        verify(repository).findById(id);
    }

    @Test
    void getAllReturnsMappedNewsList() {
        NewsEventService service = createService();
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        List<NewsEventEntity> entities = List.of(
                new NewsEventEntity(firstId, "First", "First message"),
                new NewsEventEntity(secondId, "Second", "Second message")
        );
        when(repository.findAll()).thenReturn(entities);

        NewsEventList result = service.getAll();

        assertThat(result.getEvents()).containsExactly(
                new NewsEvent(firstId, "First", "First message"),
                new NewsEvent(secondId, "Second", "Second message")
        );
        verify(repository).findAll();
    }

    @Test
    void savePersistsRequestAndReturnsSavedNews() {
        NewsEventService service = createService();
        UUID captchaId = UUID.randomUUID();
        CreateNewsEventRequest request = new CreateNewsEventRequest("Breaking", "Something happened", captchaId);
        UUID savedId = UUID.randomUUID();
        when(captchaClient.checkAndExpire(captchaId)).thenReturn(new CheckCaptchaResponse(true));
        when(repository.save(any(NewsEventEntity.class)))
                .thenAnswer(invocation -> {
                    NewsEventEntity entity = invocation.getArgument(0);
                    return new NewsEventEntity(savedId, entity.getTitle(), entity.getMessage());
                });

        NewsEvent result = service.save(request);

        ArgumentCaptor<NewsEventEntity> captor = ArgumentCaptor.forClass(NewsEventEntity.class);
        verify(repository).save(captor.capture());

        NewsEventEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getId()).isNull();
        assertThat(savedEntity.getTitle()).isEqualTo("Breaking");
        assertThat(savedEntity.getMessage()).isEqualTo("Something happened");
        assertThat(result).isEqualTo(new NewsEvent(savedId, "Breaking", "Something happened"));
        verify(captchaClient).checkAndExpire(captchaId);
    }

    @Test
    void saveThrowsBusinessExceptionWhenCaptchaWasNotFoundOrExpired() {
        NewsEventService service = createService();
        UUID captchaId = UUID.randomUUID();
        when(captchaClient.checkAndExpire(captchaId))
                .thenThrow(new CaptchaClientException(HttpStatus.NOT_FOUND, 2000, "Captcha not found or expired"));

        assertThatThrownBy(() -> service.save(new CreateNewsEventRequest("Breaking", "Something happened", captchaId)))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError())
                        .isEqualTo(BusinessError.CAPTCHA_NOT_FOUND_OR_EXPIRED));

        verify(repository, never()).save(any(NewsEventEntity.class));
    }

    @Test
    void saveThrowsBusinessExceptionWhenCaptchaWasNotSolved() {
        NewsEventService service = createService();
        UUID captchaId = UUID.randomUUID();
        when(captchaClient.checkAndExpire(captchaId))
                .thenThrow(new CaptchaClientException(HttpStatus.BAD_REQUEST, 2001, "Captcha is not solved"));

        assertThatThrownBy(() -> service.save(new CreateNewsEventRequest("Breaking", "Something happened", captchaId)))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError())
                        .isEqualTo(BusinessError.CAPTCHA_NOT_SOLVED));

        verify(repository, never()).save(any(NewsEventEntity.class));
    }

    @Test
    void saveThrowsBusinessExceptionWhenCaptchaServiceWasUnavailable() {
        NewsEventService service = createService();
        UUID captchaId = UUID.randomUUID();
        when(captchaClient.checkAndExpire(captchaId))
                .thenThrow(new RestClientException("Connection refused"));

        assertThatThrownBy(() -> service.save(new CreateNewsEventRequest("Breaking", "Something happened", captchaId)))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> assertThat(((BaseException) exception).getError())
                        .isEqualTo(BusinessError.CAPTCHA_CHECK_FAILED));

        verify(repository, never()).save(any(NewsEventEntity.class));
    }

    private NewsEventService createService() {
        return new NewsEventService(repository, new CaptchaService(captchaClient));
    }
}
