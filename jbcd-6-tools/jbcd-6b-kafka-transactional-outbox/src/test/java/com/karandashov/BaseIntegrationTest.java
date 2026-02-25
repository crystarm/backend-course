package com.karandashov;

import com.karandashov.repository.NewsEventRepository;
import com.karandashov.repository.TransactionalOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseIntegrationTest {

    @Autowired
    protected NewsEventRepository newsEventRepository;

    @Autowired
    protected TransactionalOutboxRepository transactionalOutboxRepository;

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18");

    @Container
    static ConfluentKafkaContainer kafka = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.7"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("DATABASE_URL", POSTGRES::getJdbcUrl);
        registry.add("DATABASE_USERNAME", POSTGRES::getUsername);
        registry.add("DATABASE_PASSWORD", POSTGRES::getPassword);
        registry.add("APP_DATABASE_SCHEMA", () -> "public");

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        log.info("Database started: {}", POSTGRES.getJdbcUrl());
    }

    @BeforeEach
    void tearDown() {
        newsEventRepository.deleteAll();
        transactionalOutboxRepository.deleteAll();
    }
}
