package com.karandashov;

import com.karandashov.repository.NewsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers(disabledWithoutDocker = true)
@Import(TestOAuth2ClientConfiguration.class)
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
        registry.add("spring.security.cors.allowed-origins", () -> "http://localhost:8080");
        registry.add("app.security.oidc.issuer-uri", () -> "http://localhost:5556/dex");
        registry.add("spring.security.oauth2.client.provider.dex.issuer-uri", () -> "http://localhost:5556/dex");
        registry.add("app.security.oidc.client-id", () -> "jbcd-sso-session-client");
        registry.add("spring.security.oauth2.client.registration.dex.client-id", () -> "jbcd-sso-session-client");
        registry.add("spring.security.oauth2.client.registration.dex.client-secret", () -> "jbcd-sso-session-secret");

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "public");
        registry.add("spring.datasource.hikari.schema", () -> "public");
        registry.add("spring.flyway.schemas", () -> "public");
        registry.add("spring.flyway.default-schema", () -> "public");

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
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
