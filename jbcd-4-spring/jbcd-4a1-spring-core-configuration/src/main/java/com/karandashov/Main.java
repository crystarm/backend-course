package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.service.NewsEventService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(NewsConfiguration.class);

        NewsEventService service = context.getBean(NewsEventService.class);

        var news = service.save(new CreateNewsEventRequest("Test Title", "Test Message"));
        assert news != null;
        assert "Test Title".equals(news.getTitle());
        var id = news.getId();

        var sameNews = service.getById(id);
        assert sameNews != null;
        assert "Test Message".equals(sameNews.getMessage());
    }
}