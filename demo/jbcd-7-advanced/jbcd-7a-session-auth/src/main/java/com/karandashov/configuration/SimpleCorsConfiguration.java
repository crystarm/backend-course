package com.karandashov.configuration;

import com.karandashov.configuration.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SimpleCorsConfiguration {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(CorsConfiguration configuration) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CorsConfiguration configuration() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Разрешенные origins приходят из конфигурации окружения.
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setMaxAge(168000L);
        configuration.setAllowedHeaders(List.of("*"));
        // Для cookie-based auth обязательно разрешаем credentials.
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("*"));
        return configuration;
    }
}
