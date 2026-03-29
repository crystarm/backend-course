package com.karandashov;

import com.karandashov.repository.NewsEventRepository;
import com.karandashov.repository.TransactionalOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {

    @Autowired
    protected NewsEventRepository newsEventRepository;

    @Autowired
    protected TransactionalOutboxRepository transactionalOutboxRepository;

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("DATABASE_URL", POSTGRES::getJdbcUrl);
        registry.add("DATABASE_USERNAME", POSTGRES::getUsername);
        registry.add("DATABASE_PASSWORD", POSTGRES::getPassword);
        registry.add("APP_DATABASE_SCHEMA", () -> "public");

        log.info("Database started: {}", POSTGRES.getJdbcUrl());
    }

    @BeforeEach
    void tearDown() {
        newsEventRepository.deleteAll();
        transactionalOutboxRepository.deleteAll();
    }
}
