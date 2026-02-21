package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.service.NewsEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) {

        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        NewsEventService service = context.getBean(NewsEventService.class);

        var news = service.save(new CreateNewsEventRequest("Test Title", "Test Message"));
        assert news != null;
        assert "Test Title".equals(news.getData().getTitle());
        var id = news.getData().getId();
        log.info("Saved news event: {}", news);

        var sameNews = service.getById(id);
        assert sameNews != null;
        assert "Test Message".equals(sameNews.getData().getMessage());
        log.info("Get news event: {}", sameNews);
    }
}