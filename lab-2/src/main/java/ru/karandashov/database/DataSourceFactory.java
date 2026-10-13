package ru.karandashov.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.karandashov.config.DatabaseConfig;

public final class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static HikariDataSource create(DatabaseConfig databaseConfig) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("game-database-pool");
        hikariConfig.setJdbcUrl(databaseConfig.getJdbcUrl());
        hikariConfig.setUsername(databaseConfig.getUsername());
        hikariConfig.setPassword(databaseConfig.getPassword());
        hikariConfig.setSchema(databaseConfig.getSchema());
        hikariConfig.setMaximumPoolSize(databaseConfig.getMaximumPoolSize());
        hikariConfig.setMinimumIdle(databaseConfig.getMinimumIdle());
        hikariConfig.setConnectionTimeout(databaseConfig.getConnectionTimeoutMs());
        hikariConfig.setInitializationFailTimeout(databaseConfig.getConnectionTimeoutMs());
        hikariConfig.addDataSourceProperty("ApplicationName", "lab-2-game");
        return new HikariDataSource(hikariConfig);
    }
}
