package com.karandashov.listener;

import com.karandashov.controller.SseController;
import com.karandashov.dto.NewsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsEventRedisListener implements MessageListener {

    private final SseController sseController;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onMessage(@NonNull Message message, byte[] pattern) {
        try {
            NewsEvent newsEvent = (NewsEvent) redisTemplate.getValueSerializer().deserialize(message.getBody());
            log.info("Received news event from Redis: {}", newsEvent);
            sseController.broadcast(newsEvent);
        } catch (Exception e) {
            log.error("Error processing Redis message", e);
        }
    }
}
