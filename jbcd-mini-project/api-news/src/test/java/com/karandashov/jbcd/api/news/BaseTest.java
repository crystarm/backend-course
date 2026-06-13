package com.karandashov.jbcd.api.news;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.karandashov.jbcd.api.news.repository.NewsEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.json.JsonMapper;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Slf4j
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTest {

    private static final WireMockServer WIREMOCK = new WireMockServer(
            wireMockConfig()
                    .dynamicPort()
                    .usingFilesUnderClasspath("wiremock")
    );

    static {
        WIREMOCK.start();
    }

    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer(DockerImageName.parse("postgres:18"));

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:8.6.0"))
                    .withExposedPorts(6379);

    @Autowired
    protected NewsEventRepository newsEventRepository;

    @Autowired
    protected CacheManager cacheManager;

    @Autowired
    protected JsonMapper jsonMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "public");
        registry.add("spring.datasource.hikari.schema", () -> "public");
        registry.add("spring.flyway.schemas", () -> "public");
        registry.add("spring.flyway.default-schema", () -> "public");

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));

        registry.add("client.captcha.base-url", WIREMOCK::baseUrl);

        log.info("Database started: {}", POSTGRES.getJdbcUrl());
        log.info("Redis started: {}:{}", REDIS.getHost(), REDIS.getMappedPort(6379));
        log.info("Captcha api mock started: {}", WIREMOCK.baseUrl());
    }

    @AfterEach
    void tearDown() {
        newsEventRepository.deleteAll();
        cacheManager.getCacheNames().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
        WIREMOCK.resetAll();
    }

    protected WireMockServer captchaApiMock() {
        return WIREMOCK;
    }
}
