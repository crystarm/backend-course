package com.karandashov;

import com.karandashov.repository.NewsEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers(disabledWithoutDocker = true)
public abstract class BaseTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18");

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @Autowired
    protected NewsEventRepository newsEventRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.cors.allowed-origins", () -> "http://localhost:8080");
        registry.add("app.security.oidc.issuer-uri", () -> "http://localhost:5556/dex");
        registry.add("spring.security.oauth2.client.provider.dex.issuer-uri", () -> "http://localhost:5556/dex");
        registry.add("app.security.oidc.client-id", () -> "jbcd-jwt-frontend");
        registry.add("spring.security.oauth2.client.registration.dex.client-id", () -> "jbcd-jwt-frontend");
        registry.add("app.security.oidc.frontend-base-url", () -> "http://localhost:8080");

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
    }
}
