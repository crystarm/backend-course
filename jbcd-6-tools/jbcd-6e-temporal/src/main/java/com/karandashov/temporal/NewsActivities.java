package com.karandashov.temporal;

import com.karandashov.dto.NewsEvent;
import com.karandashov.dto.NewsStatus;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface NewsActivities {

    boolean validateTitle(String title);

    boolean validateMessage(String message);

    NewsEvent finalizeNews(NewsEvent event, NewsStatus status);
}
