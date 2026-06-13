package com.karandashov.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesService {

    private static final Logger log = LoggerFactory.getLogger(PropertiesService.class);

    private final Properties properties;

    public PropertiesService() {
        this.properties = new Properties();
        try (InputStream in = PropertiesService.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new IOException("application.properties not found");
            }
            properties.load(in);
        } catch (IOException e) {
            log.error("В ресурсах не обнаружен application.properties файл с настройками", e);
            throw new RuntimeException("В ресурсах не обнаружен application.properties файл с настройками", e);
        }
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log.error("В application.properties файле не задано значение для ключа {}", key);
            throw new RuntimeException("В application.properties файле не задано значение для ключа " + key);
        }
        return value;
    }
}
