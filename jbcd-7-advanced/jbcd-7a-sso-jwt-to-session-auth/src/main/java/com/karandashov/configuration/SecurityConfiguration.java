package com.karandashov.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                // Оставляем стандартный CORS-конфиг, чтобы frontend можно было вынести отдельно.
                .cors(spec -> spec.configurationSource(corsConfigurationSource))
                // Для cookie-сессии нужна CSRF-защита с токеном в cookie.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(authorize -> authorize
                        // Статика, login page и oauth2 callback должны быть публичными.
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/login", "/login.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll()
                        // Чтение новостей и проверка текущей сессии доступны без логина.
                        .requestMatchers(HttpMethod.GET, "/api/news", "/api/news/*", "/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // Используем свою статическую страницу входа.
                        .loginPage("/login")
                        // После SSO возвращаем пользователя на главную страницу приложения.
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        // Logout очищает только локальную сессию приложения.
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("SESSION")
                );

        return http.build();
    }
}
