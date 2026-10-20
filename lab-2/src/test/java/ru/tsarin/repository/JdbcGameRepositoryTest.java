package ru.tsarin.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.tsarin.config.DatabaseConfig;
import ru.tsarin.database.DataSourceFactory;
import ru.tsarin.database.DatabaseMigrator;
import ru.tsarin.entity.GameEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
class JdbcGameRepositoryTest {

    @Container
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("game_test_database")
            .withUsername("game_test_app")
            .withPassword("game_test_password");

    private static HikariDataSource dataSource;
    private static JdbcGameRepository repository;

    @BeforeAll
    static void setUpDatabase() {
        DatabaseConfig config = new DatabaseConfig(
                POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(),
                POSTGRES.getPassword(),
                "game_test_catalog",
                4,
                1,
                10_000
        );
        dataSource = DataSourceFactory.create(config);
        DatabaseMigrator.migrate(dataSource, config.getSchema());
        repository = new JdbcGameRepository(dataSource);
    }

    @AfterAll
    static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @BeforeEach
    void clearTable() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE game RESTART IDENTITY");
        }
    }

    @Test
    void savesAndFindsGameById() {
        int id = repository.save(new GameEntity("Disco Elysium", 9.6));

        assertEquals(new GameEntity(id, "Disco Elysium", 9.6), repository.findById(id));
        assertNull(repository.findById(id + 1));
    }

    @Test
    void findsByTitleAndReturnsAllInIdOrder() {
        int firstId = repository.save(new GameEntity("Portal 2", 9.5));
        int secondId = repository.save(new GameEntity("Half-Life 2", 9.7));

        assertEquals(new GameEntity(firstId, "Portal 2", 9.5), repository.findByField("Portal 2"));
        assertNull(repository.findByField("Unknown game"));
        assertEquals(List.of(
                new GameEntity(firstId, "Portal 2", 9.5),
                new GameEntity(secondId, "Half-Life 2", 9.7)
        ), repository.findAll());
    }

    @Test
    void updatesOnlyExistingGame() {
        int id = repository.save(new GameEntity("Baldur's Gate 3", 9.6));
        GameEntity updatedGame = new GameEntity(id, "Baldur's Gate 3", 9.8);

        assertTrue(repository.update(updatedGame));
        assertTrue(repository.update(updatedGame));
        assertEquals(updatedGame, repository.findById(id));
        assertFalse(repository.update(new GameEntity(id + 1, "Missing", 1.0)));
    }

    @Test
    void deletesGameIdempotently() {
        int id = repository.save(new GameEntity("Hades", 9.3));

        repository.deleteById(id);
        repository.deleteById(id);

        assertNull(repository.findById(id));
    }
}
