package com.karandashov.configuration;

import com.karandashov.configuration.properties.OidcProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CorsConfigurationSource corsConfigurationSource;
    private final OidcProperties oidcProperties;

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

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        // Явно описываем client registration, чтобы не зависеть от auto-configuration.
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("dex")
                .clientName("Dex")
                .clientId(oidcProperties.getClientId())
                .clientSecret(oidcProperties.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .issuerUri(oidcProperties.getIssuerUri())
                .authorizationUri(oidcProperties.getIssuerUri() + "/auth")
                .tokenUri(oidcProperties.getIssuerUri() + "/token")
                .userInfoUri(oidcProperties.getIssuerUri() + "/userinfo")
                .jwkSetUri(oidcProperties.getIssuerUri() + "/keys")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .build();
        return new InMemoryClientRegistrationRepository(clientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
