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
        registry.add("CORS_ALLOWED_ORIGINS", () -> "http://localhost:8080");
        registry.add("OIDC_ISSUER_URI", () -> "http://localhost:5556/dex");
        registry.add("OIDC_CLIENT_ID", () -> "jbcd-jwt-frontend");
        registry.add("FRONTEND_BASE_URL", () -> "http://localhost:8080");

        registry.add("DATABASE_URL", POSTGRES::getJdbcUrl);
        registry.add("DATABASE_USERNAME", POSTGRES::getUsername);
        registry.add("DATABASE_PASSWORD", POSTGRES::getPassword);
        registry.add("APP_DATABASE_SCHEMA", () -> "public");

        log.info("Database started: {}", POSTGRES.getJdbcUrl());
    }

    @BeforeEach
    void tearDown() {
        newsEventRepository.deleteAll();
    }
}
