package com.karandashov.jbcd.api.captcha;

import com.karandashov.jbcd.api.captcha.entity.CaptchaEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTest {

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:8.6.0"))
                    .withExposedPorts(6379);

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @Autowired
    protected JsonMapper jsonMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("REDIS_HOST", REDIS::getHost);
        registry.add("REDIS_PORT", () -> REDIS.getMappedPort(6379));

        log.info("Redis started: {}:{}", REDIS.getHost(), REDIS.getMappedPort(6379));
    }

    @AfterEach
    void tearDown() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    protected void saveCaptcha(CaptchaEntity captcha, Duration ttl) {
        stringRedisTemplate.opsForValue()
                .set(buildCaptchaKey(captcha.getId()), jsonMapper.writeValueAsString(captcha), ttl);
    }

    protected Optional<CaptchaEntity> findCaptcha(UUID id) {
        String value = stringRedisTemplate.opsForValue().get(buildCaptchaKey(id));
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(jsonMapper.readValue(value, CaptchaEntity.class));
    }

    protected Long getCaptchaTtlSeconds(UUID id) {
        return stringRedisTemplate.getExpire(buildCaptchaKey(id));
    }

    private String buildCaptchaKey(UUID id) {
        return CAPTCHA_KEY_PREFIX + id;
    }
}
