package com.karandashov;

import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:8.6.0"))
                    .withExposedPorts(6379);

    @Autowired
    protected NewsEventRepository newsEventRepository;

    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("CORS_ALLOWED_ORIGINS", () -> "http://localhost:8080");
        registry.add("OIDC_ISSUER_URI", () -> "http://localhost:5556/dex");
        registry.add("OIDC_CLIENT_ID", () -> "jbcd-sso-session-client");
        registry.add("OIDC_CLIENT_SECRET", () -> "jbcd-sso-session-secret");

        registry.add("DATABASE_URL", POSTGRES::getJdbcUrl);
        registry.add("DATABASE_USERNAME", POSTGRES::getUsername);
        registry.add("DATABASE_PASSWORD", POSTGRES::getPassword);
        registry.add("APP_DATABASE_SCHEMA", () -> "public");

        registry.add("REDIS_HOST", REDIS::getHost);
        registry.add("REDIS_PORT", () -> REDIS.getMappedPort(6379));
    }

    @BeforeEach
    void tearDown() {
        newsEventRepository.deleteAll();
        deleteRedisKeys();
    }

    private void deleteRedisKeys() {
        var keys = stringRedisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }
}
