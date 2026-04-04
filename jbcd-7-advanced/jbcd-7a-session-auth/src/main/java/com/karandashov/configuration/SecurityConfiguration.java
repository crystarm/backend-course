package com.karandashov.configuration;

import com.karandashov.dto.UniversalResponse;
import com.karandashov.dto.auth.AuthStateResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JsonMapper jsonMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(spec -> spec.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/login", "/login.html", "/css/**", "/js/**", "/fragments/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/news", "/api/news/*", "/api/auth/me").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/registration", "/api/auth/login", "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) ->
                                writeJson(response, HttpServletResponse.SC_OK,
                                        new UniversalResponse<>(new AuthStateResponse(true, authentication.getName()))))
                        .failureHandler((request, response, exception) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        new UniversalResponse<>(4010, "Неверный email или пароль")))
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                writeJson(response, HttpServletResponse.SC_OK,
                                        new UniversalResponse<>(new AuthStateResponse(false, null))))
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        new UniversalResponse<>(4010, "Требуется аутентификация")))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            boolean csrfProblem = accessDeniedException instanceof MissingCsrfTokenException
                                    || accessDeniedException instanceof InvalidCsrfTokenException;
                            var authentication = SecurityContextHolder.getContext().getAuthentication();
                            boolean anonymous = authentication == null
                                    || !authentication.isAuthenticated()
                                    || "anonymousUser".equals(authentication.getPrincipal());

                            if (!csrfProblem && anonymous) {
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        new UniversalResponse<>(4010, "Требуется аутентификация"));
                                return;
                            }

                            writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                                    new UniversalResponse<>(4030, "Доступ запрещен"));
                        })
                )
                .addFilterAfter(csrfCookieFilter(), CsrfFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter csrfCookieFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    // Важно обратиться к токену, чтобы репозиторий записал cookie.
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void writeJson(HttpServletResponse response,
                           int status,
                           UniversalResponse<?> body) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        jsonMapper.writeValue(response.getWriter(), body);
    }
}
