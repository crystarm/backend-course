package ru.karandashov.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

public final class DatabaseConfig {

    private static final String CONFIG_FILE = "application.properties";
    private static final Pattern SQL_IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String schema;
    private final int maximumPoolSize;
    private final int minimumIdle;
    private final long connectionTimeoutMs;

    public DatabaseConfig(
            String jdbcUrl,
            String username,
            String password,
            String schema,
            int maximumPoolSize,
            int minimumIdle,
            long connectionTimeoutMs
    ) {
        this.jdbcUrl = requireNotBlank(jdbcUrl, "jdbcUrl");
        this.username = requireNotBlank(username, "username");
        this.password = requireNotBlank(password, "password");
        this.schema = validateSchema(schema);
        if (maximumPoolSize < 1) {
            throw new IllegalArgumentException("maximumPoolSize должен быть положительным");
        }
        if (minimumIdle < 0 || minimumIdle > maximumPoolSize) {
            throw new IllegalArgumentException("minimumIdle должен быть от 0 до maximumPoolSize");
        }
        if (connectionTimeoutMs < 250) {
            throw new IllegalArgumentException("connectionTimeoutMs не может быть меньше 250 мс");
        }
        this.maximumPoolSize = maximumPoolSize;
        this.minimumIdle = minimumIdle;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public static DatabaseConfig load() {
        Properties properties = loadProperties();
        return new DatabaseConfig(
                read(properties, "database.url", "DB_URL"),
                read(properties, "database.username", "DB_USERNAME"),
                read(properties, "database.password", "DB_PASSWORD"),
                read(properties, "database.schema", "DB_SCHEMA"),
                readInt(properties, "database.pool.maximum-size", "DB_POOL_MAX_SIZE"),
                readInt(properties, "database.pool.minimum-idle", "DB_POOL_MIN_IDLE"),
                readLong(properties, "database.pool.connection-timeout-ms", "DB_CONNECTION_TIMEOUT_MS")
        );
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Файл конфигурации не найден: " + CONFIG_FILE);
            }
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать " + CONFIG_FILE, e);
        }
    }

    private static String read(Properties properties, String propertyName, String environmentName) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        String systemValue = System.getProperty(propertyName);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String propertyValue = properties.getProperty(propertyName);
        if (propertyValue == null || propertyValue.isBlank()) {
            throw new IllegalStateException("Не задан параметр " + propertyName);
        }
        return propertyValue;
    }

    private static int readInt(Properties properties, String propertyName, String environmentName) {
        try {
            return Integer.parseInt(read(properties, propertyName, environmentName));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Параметр " + propertyName + " должен быть целым числом", e);
        }
    }

    private static long readLong(Properties properties, String propertyName, String environmentName) {
        try {
            return Long.parseLong(read(properties, propertyName, environmentName));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Параметр " + propertyName + " должен быть целым числом", e);
        }
    }

    private static String requireNotBlank(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " не может быть null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        return value;
    }

    private static String validateSchema(String schema) {
        String value = requireNotBlank(schema, "schema");
        if (!SQL_IDENTIFIER.matcher(value).matches()) {
            throw new IllegalArgumentException("Некорректное имя схемы: " + value);
        }
        return value;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSchema() {
        return schema;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public long getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }
}
