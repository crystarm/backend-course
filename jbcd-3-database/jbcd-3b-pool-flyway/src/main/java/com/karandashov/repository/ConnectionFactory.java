package com.karandashov.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private final DataSource dataSource;

    public ConnectionFactory(String url, String user, String password, String schema) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");

        // Настройки пула (опционально, можно настроить под ваши нужды)
        config.setMaximumPoolSize(10); // Максимальное количество соединений в пуле
        config.setMinimumIdle(5); // Минимальное количество простаивающих соединений
        config.setIdleTimeout(300000); // Время простоя соединения (в миллисекундах, 5 минут)
        config.setMaxLifetime(1800000); // Максимальное время жизни соединения (30 минут)
        config.setConnectionTimeout(30000); // Таймаут ожидания соединения (30 секунд)

        config.setSchema(schema);

        // Инициализация пула
        dataSource = new HikariDataSource(config);

        // чтобы избежать утечек ресурсов БД (!!!), закрываем пул соединений, когда приложение завершает работу
        Runtime.getRuntime().addShutdownHook(new Thread(this::closeDataSource));
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Метод для закрытия пула (вызывать при завершении приложения)
    public void closeDataSource() {
        if (dataSource != null && dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }
}
