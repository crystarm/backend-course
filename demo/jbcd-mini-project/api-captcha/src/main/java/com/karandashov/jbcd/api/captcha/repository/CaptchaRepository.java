package com.karandashov.jbcd.api.captcha.repository;

import com.karandashov.jbcd.api.captcha.entity.CaptchaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CaptchaRepository {

    private static final String KEY_PREFIX = "captcha:";

    private final StringRedisTemplate redisTemplate;

    private final JsonMapper jsonMapper;

    public void save(CaptchaEntity entity, Duration ttl) {
        redisTemplate.opsForValue().set(buildKey(entity.getId()), serialize(entity), ttl);
    }

    public Optional<CaptchaEntity> findById(UUID id) {
        String value = redisTemplate.opsForValue().get(buildKey(id));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(deserialize(value));
    }

    public void deleteById(UUID id) {
        redisTemplate.delete(buildKey(id));
    }

    private String buildKey(UUID id) {
        return KEY_PREFIX + id;
    }

    private String serialize(CaptchaEntity entity) {
        return jsonMapper.writeValueAsString(entity);
    }

    private CaptchaEntity deserialize(String value) {
        return jsonMapper.readValue(value, CaptchaEntity.class);
    }
}
