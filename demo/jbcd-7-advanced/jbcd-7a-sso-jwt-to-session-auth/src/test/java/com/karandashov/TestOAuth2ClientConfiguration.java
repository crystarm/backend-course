package com.karandashov;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@TestConfiguration
public class TestOAuth2ClientConfiguration {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration dexClientRegistration = ClientRegistration.withRegistrationId("dex")
                .clientId("jbcd-sso-session-client")
                .clientSecret("jbcd-sso-session-secret")
                .clientName("Dex")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                // В тестах используем статические endpoint'ы, чтобы не ходить в discovery.
                .authorizationUri("http://localhost:5556/dex/auth")
                .tokenUri("http://localhost:5556/dex/token")
                .userInfoUri("http://localhost:5556/dex/userinfo")
                .jwkSetUri("http://localhost:5556/dex/keys")
                .issuerUri("http://localhost:5556/dex")
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .scope("openid", "profile", "email")
                .build();

        return new InMemoryClientRegistrationRepository(dexClientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository
    ) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }
}
