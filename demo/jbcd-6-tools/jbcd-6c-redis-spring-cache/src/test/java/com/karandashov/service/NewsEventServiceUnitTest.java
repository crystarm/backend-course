package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.exceptions.NotFoundException;
import com.karandashov.mapper.NewsMapper;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsEventServiceUnitTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private NewsEventRepository repository;

    @Mock
    private NewsMapper mapper;

    @InjectMocks
    private NewsEventService service;

    @Test
    void getById_Success() {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");
        NewsEvent dto = new NewsEvent(id, "Title", "Message");

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        NewsEvent response = service.getById(id);

        assertNotNull(response);
        assertEquals(dto, response);

        verify(repository).findById(id);
        verify(mapper).toDto(entity);
    }

    @Test
    void getById_NotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getById(id));
        assertEquals("Не смог найти новость по id", exception.getMessage());

        verify(repository).findById(id);
        verifyNoInteractions(mapper);
    }

    @Test
    void getAll_Success() {
        NewsEventEntity entity = new NewsEventEntity(UUID.randomUUID(), "Title", "Message");
        NewsEvent dto = new NewsEvent(entity.getId(), "Title", "Message");
        List<NewsEventEntity> entities = List.of(entity);
        List<NewsEvent> dtos = List.of(dto);

        when(repository.findAll()).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        NewsEventList response = service.getAll();

        assertNotNull(response);
        assertEquals(1, response.getEvents().size());
        assertTrue(response.getEvents().contains(dto));

        verify(repository).findAll();
        verify(mapper).toDtoList(entities);
    }

    @Test
    void save_Success() {
        CreateNewsEventRequest request = new CreateNewsEventRequest("New Title", "New Message");
        NewsEventEntity savedEntity = NewsEventEntity.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();
        NewsEvent dto = new NewsEvent(savedEntity.getId(), savedEntity.getTitle(), savedEntity.getMessage());

        when(repository.save(any(NewsEventEntity.class))).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(dto);

        NewsEvent response = service.save(request);

        assertNotNull(response);
        assertEquals(dto, response);

        verify(repository).save(argThat(entity ->
                entity.getTitle().equals(request.getTitle()) &&
                        entity.getMessage().equals(request.getMessage()) &&
                        entity.getId() != null
        ));
        verify(mapper).toDto(savedEntity);
    }
}
