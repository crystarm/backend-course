package com.karandashov.service;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.NewsEventList;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
public class NewsEventCacheTest {

    // @MockitoSpyBean
    @Autowired
    private NewsEventRepository repository;

    @Autowired
    private NewsEventService service;

    @Test
    void testCaching() {
        // 1. Initial call to getAll()
        UniversalResponse<NewsEventList> response1 = service.getAll();
        // verify(repository, times(1)).getAll();

        // 2. Second call to getAll() - should be cached
        UniversalResponse<NewsEventList> response2 = service.getAll();
        // Repository should not be called again
        // verify(repository, times(1)).getAll();
        // Since it's the same object returned by the aspect (not deep cloned)
        assertSame(response1, response2, "Should return the same instance from cache");

        // 3. Save a new news - should clear cache
        service.save(new CreateNewsEventRequest("Title", "Message"));

        // 4. Third call to getAll() - should be a cache miss
        UniversalResponse<NewsEventList> response3 = service.getAll();
        // verify(repository, times(2)).getAll();

        assertEquals(1, response3.getData().getEvents().size());
    }
}
