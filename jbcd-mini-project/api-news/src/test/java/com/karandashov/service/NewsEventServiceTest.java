package com.karandashov.service;

import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.request.CreateNewsEventRequest;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.exceptions.BusinessError;
import com.karandashov.jbcd.utils.web.exception.BaseException;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsEventServiceTest {

    @Mock
    private NewsEventRepository repository;

    @InjectMocks
    private NewsEventService service;

    @Test
    void getByIdReturnsMappedNewsEvent() {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        NewsEvent result = service.getById(id);

        assertThat(result).isEqualTo(new NewsEvent(id, "Title", "Message"));
        verify(repository).findById(id);
    }

    @Test
    void getByIdThrowsBusinessExceptionWhenNewsWasNotFound() {
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
        CreateNewsEventRequest request = new CreateNewsEventRequest("Breaking", "Something happened");
        UUID savedId = UUID.randomUUID();
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
    }
}
