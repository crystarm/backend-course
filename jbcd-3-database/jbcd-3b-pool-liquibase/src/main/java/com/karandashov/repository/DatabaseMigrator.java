package com.karandashov.repository;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DatabaseMigrator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrator.class);
    private final ConnectionFactory connectionFactory;

    public DatabaseMigrator(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void runMigrations() {
        try (Connection connection = connectionFactory.getConnection()) {
            // Указываем путь к мастер-файлу миграций
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection)
            );

            // Применяем миграции (можно задавать разный контекст в зависимости от стенда - не очень хорошая практика)
            liquibase.update();
            System.out.println("Миграции успешно применены!");
        } catch (Exception e) {
            log.error("Ошибка применения миграций БД: ", e);
            throw new RuntimeException("Ошибка при выполнении миграций", e);
        }
    }
}