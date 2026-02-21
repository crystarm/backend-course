package com.karandashov.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("News API")
                .version("1.0.0")
                .description("Demonstrates OPEN API")
                .license(new License()
                    .name("BSD Zero Clause License")
                    .url("https://opensource.org/license/0bsd")));
    }
}