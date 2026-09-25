package com.karandashov.jbcd.api.news.configuration;

import com.karandashov.jbcd.client.captcha.client.CaptchaClient;
import com.karandashov.jbcd.client.captcha.client.CaptchaClientException;
import com.karandashov.jbcd.client.captcha.response.CaptchaErrorResponse;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.util.StreamUtils;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class CaptchaClientConfiguration {

    @Bean
    public CaptchaClient captchaClient(
            @Value("${client.captcha.base-url}") String baseUrl,
            ObservationRegistry observationRegistry,
            JsonMapper jsonMapper
    ) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .observationRegistry(observationRegistry)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            CaptchaErrorResponse error = readErrorResponse(response, jsonMapper);
                            throw new CaptchaClientException(
                                    response.getStatusCode(),
                                    error == null ? null : error.code(),
                                    error == null ? "Captcha service request failed" : error.message()
                            );
                        }
                )
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(CaptchaClient.class);
    }

    private CaptchaErrorResponse readErrorResponse(ClientHttpResponse response, JsonMapper jsonMapper) throws IOException {
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            return null;
        }
        return jsonMapper.readValue(body, CaptchaErrorResponse.class);
    }
}
