package com.karandashov.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(spec -> spec.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable) // не нужен для обычного jwt в заголовке Authorization
                .authorizeHttpRequests(authorize -> authorize
                        // Статика и публичные страницы доступны без сессии
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/login", "/login.html", "/css/**", "/js/**", "/fragments/**").permitAll()
                        // Публичные API только на чтение и проверка текущей сессии
                        .requestMatchers(HttpMethod.GET, "/api/news", "/api/news/*", "/api/auth/me").permitAll()
                        // Регистрация, логин и logout доступны по стандартному security flow
                        .requestMatchers(HttpMethod.POST, "/api/auth/registration", "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
        // todo resource server auth
        ;

        return http.build();
    }
}
