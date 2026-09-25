package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.entity.NewsEventEntity;
import com.karandashov.mapper.NewsMapperImpl;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {
        NewsEventController.class,
        NewsEventService.class,
        NewsMapperImpl.class
})
public class NewsEventControllerTest {

    @MockitoBean
    private NewsEventRepository repository;

    @Autowired
    private NewsEventController controller;

    @Test
    void getAll_Success() {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");
        when(repository.findAll()).thenReturn(List.of(entity));

        NewsEventList actualResponse = controller.getAll();

        assertNotNull(actualResponse);
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getEvents().size());
        NewsEvent dto = actualResponse.getEvents().iterator().next();
        assertEquals(id, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("Message", dto.getMessage());

        verify(repository).findAll();
    }

    @Test
    void getById_Success() {
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");
        when(repository.findById(id)).thenReturn(Optional.of(entity));

        NewsEvent actualResponse = controller.getById(id);

        assertNotNull(actualResponse);
        assertEquals(id, actualResponse.getId());
        assertEquals("Title", actualResponse.getTitle());
        assertEquals("Message", actualResponse.getMessage());

        verify(repository).findById(id);
    }

    @Test
    void save_Success() {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Title", "Message");
        UUID id = UUID.randomUUID();
        NewsEventEntity entity = new NewsEventEntity(id, "Title", "Message");

        when(repository.save(any(NewsEventEntity.class))).thenReturn(entity);

        NewsEvent actualResponse = controller.save(request);

        assertNotNull(actualResponse);
        assertEquals(id, actualResponse.getId());
        assertEquals("Title", actualResponse.getTitle());
        assertEquals("Message", actualResponse.getMessage());

        verify(repository).save(any(NewsEventEntity.class));
    }
}
