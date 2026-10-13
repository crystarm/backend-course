package ru.karandashov.database;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public final class DatabaseMigrator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);

    private DatabaseMigrator() {
    }

    public static void migrate(DataSource dataSource, String schema) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schema)
                .defaultSchema(schema)
                .createSchemas(true)
                .locations("classpath:db/migration")
                .load();

        MigrateResult result = flyway.migrate();
        log.info("Миграции Flyway завершены: применено {}, текущая версия {}",
                result.migrationsExecuted, result.targetSchemaVersion);
    }
}
