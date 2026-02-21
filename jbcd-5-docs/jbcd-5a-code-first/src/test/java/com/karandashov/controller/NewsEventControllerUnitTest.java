package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.service.NewsEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class NewsEventControllerUnitTest {

    private NewsEventService service;
    private NewsEventController controller;

    @BeforeEach
    void setUp() {
        service = mock(NewsEventService.class);
        controller = new NewsEventController(service);
    }

    @Test
    void getAll_Success() {
        NewsEvent dto = new NewsEvent(UUID.randomUUID(), "Title", "Message");
        NewsEventList list = new NewsEventList(List.of(dto));
        UniversalResponse<NewsEventList> expectedResponse = new UniversalResponse<>(list);

        when(service.getAll()).thenReturn(expectedResponse);

        UniversalResponse<NewsEventList> actualResponse = controller.getAll();

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(service).getAll();
    }

    @Test
    void getById_Success() {
        UUID id = UUID.randomUUID();
        NewsEvent dto = new NewsEvent(id, "Title", "Message");
        UniversalResponse<NewsEvent> expectedResponse = new UniversalResponse<>(dto);

        when(service.getById(id)).thenReturn(expectedResponse);

        UniversalResponse<NewsEvent> actualResponse = controller.getById(id);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(service).getById(id);
    }

    @Test
    void save_Success() {
        CreateNewsEventRequest request = new CreateNewsEventRequest("Title", "Message");
        NewsEvent dto = new NewsEvent(UUID.randomUUID(), "Title", "Message");
        UniversalResponse<NewsEvent> expectedResponse = new UniversalResponse<>(dto);

        when(service.save(request)).thenReturn(expectedResponse);

        UniversalResponse<NewsEvent> actualResponse = controller.save(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(service).save(request);
    }
}
