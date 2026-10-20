package ru.tsarin;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tsarin.config.DatabaseConfig;
import ru.tsarin.database.DataSourceFactory;
import ru.tsarin.database.DatabaseMigrator;
import ru.tsarin.entity.GameEntity;
import ru.tsarin.repository.GameRepository;
import ru.tsarin.repository.JdbcGameRepository;

public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {
        DatabaseConfig databaseConfig = DatabaseConfig.load();

        try (HikariDataSource dataSource = DataSourceFactory.create(databaseConfig)) {
            DatabaseMigrator.migrate(dataSource, databaseConfig.getSchema());
            GameRepository repository = new JdbcGameRepository(dataSource);
            demonstrateCrud(repository);
        }
    }

    private static void demonstrateCrud(GameRepository repository) {
        int witcherId = repository.save(new GameEntity("The Witcher 3", 9.7));
        int portalId = repository.save(new GameEntity("Portal 2", 9.5));
        log.info("save: созданы игры с id={} и id={}", witcherId, portalId);

        log.info("findById: {}", repository.findById(witcherId));
        log.info("findByField: {}", repository.findByField("Portal 2"));
        log.info("findAll: {}", repository.findAll());

        boolean updated = repository.update(new GameEntity(witcherId, "The Witcher 3: Wild Hunt", 9.8));
        log.info("update: результат={}, сущность={}", updated, repository.findById(witcherId));

        repository.deleteById(portalId);
        repository.deleteById(portalId);
        repository.deleteById(witcherId);
        log.info("deleteById: после повторного удаления результат поиска={}", repository.findById(portalId));
    }
}
