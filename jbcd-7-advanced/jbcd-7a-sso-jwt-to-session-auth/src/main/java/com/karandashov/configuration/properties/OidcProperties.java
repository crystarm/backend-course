package com.karandashov.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.security.oidc")
public class OidcProperties {

    private String issuerUri;

    private String clientId;

    private String clientSecret;
}
