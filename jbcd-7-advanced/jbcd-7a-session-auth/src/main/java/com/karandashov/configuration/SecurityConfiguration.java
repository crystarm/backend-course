package com.karandashov.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // todo сделать правильный csrf
                .cors(spec -> spec.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(authorize -> authorize
                        // добавляем исключения аутентификации
                        .requestMatchers(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/**/news/**")).permitAll() // fixme не уверен что это так работает, нужен тест
                        // todo добавить в исключения регистрацию и html страницы из MvcConfig
                        .anyRequest().authenticated() // все остальное должно пройти аутентификацию
                )
                // todo correct session login with redis
                .formLogin(customizer -> customizer
                        .loginPage("/api/auth") // ? todo correct
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/auth.html") // ?todo correct
                        .permitAll()
                )
                .logout(customizer -> customizer
                        .logoutSuccessUrl("/")
                        .permitAll())

        ;
        return http.build();
    }


}