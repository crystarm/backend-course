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
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "public");
        registry.add("spring.datasource.hikari.schema", () -> "public");
        registry.add("spring.flyway.schemas", () -> "public");
        registry.add("spring.flyway.default-schema", () -> "public");

        log.info("Database started: {}", POSTGRES.getJdbcUrl());
    }

    @BeforeEach
    void tearDown() {
        newsEventRepository.deleteAll();
        transactionalOutboxRepository.deleteAll();
    }
}
