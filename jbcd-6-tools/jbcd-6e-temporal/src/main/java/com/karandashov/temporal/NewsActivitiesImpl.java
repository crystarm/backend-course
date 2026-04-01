package com.karandashov.temporal;

import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsStatus;
import io.temporal.spring.boot.ActivityImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// Говорим starter'у Temporal, что это activity-реализация для указанной task queue.
@ActivityImpl(taskQueues = "news-event-task-queue")
public class NewsActivitiesImpl implements NewsActivities {

    @Override
    public boolean validateTitle(String title) {
        boolean valid = title != null && title.length() > 3 && title.length() < 30;
        log.info("Temporal activity validateTitle finished. title='{}', valid={}", title, valid);
        return valid;
    }

    @Override
    public boolean validateMessage(String message) {
        boolean valid = message != null && message.length() > 10;
        log.info("Temporal activity validateMessage finished. messageLength={}, valid={}",
                message == null ? 0 : message.length(), valid);
        return valid;
    }

    @Override
    public NewsEvent finalizeNews(NewsEvent event, NewsStatus status) {
        NewsEvent updatedEvent = new NewsEvent(event.getId(), event.getTitle(), event.getMessage(), status);
        log.info("Temporal activity finalizeNews finished. newsId={}, status={}", event.getId(), status);
        return updatedEvent;
    }
}
