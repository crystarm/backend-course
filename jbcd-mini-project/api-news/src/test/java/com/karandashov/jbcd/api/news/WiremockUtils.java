package com.karandashov.jbcd.api.news;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public final class WiremockUtils {

    public static final UUID SOLVED_CAPTCHA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID CAPTCHA_NOT_FOUND_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID CAPTCHA_NOT_SOLVED_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final UUID CAPTCHA_CHECK_FAILED_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private WiremockUtils() {
    }

    public static void stubSolvedCaptcha(WireMockServer wireMockServer) {
        stubSolvedCaptcha(wireMockServer, SOLVED_CAPTCHA_ID);
    }

    public static void stubSolvedCaptcha(WireMockServer wireMockServer, UUID captchaId) {
        wireMockServer.stubFor(post(urlEqualTo(captchaCheckPath(captchaId)))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("captcha/check-and-expire/response/success.json")));
    }

    public static void stubCaptchaNotFoundOrExpired(WireMockServer wireMockServer) {
        stubCaptchaError(
                wireMockServer,
                CAPTCHA_NOT_FOUND_ID,
                404,
                "captcha/check-and-expire/response/not-found.json"
        );
    }

    public static void stubCaptchaNotSolved(WireMockServer wireMockServer) {
        stubCaptchaError(
                wireMockServer,
                CAPTCHA_NOT_SOLVED_ID,
                400,
                "captcha/check-and-expire/response/not-solved.json"
        );
    }

    public static void stubCaptchaCheckFailed(WireMockServer wireMockServer) {
        stubCaptchaError(
                wireMockServer,
                CAPTCHA_CHECK_FAILED_ID,
                500,
                "captcha/check-and-expire/response/internal-error.json"
        );
    }

    public static void verifyCaptchaCheckRequested(WireMockServer wireMockServer, UUID captchaId) {
        wireMockServer.verify(postRequestedFor(urlEqualTo(captchaCheckPath(captchaId))));
    }

    public static void verifyCaptchaCheckRequestedWithTraceparent(WireMockServer wireMockServer, UUID captchaId) {
        wireMockServer.verify(postRequestedFor(urlEqualTo(captchaCheckPath(captchaId)))
                .withHeader("traceparent", matching(".+")));
    }

    public static void verifyCaptchaCheckWasNotRequested(WireMockServer wireMockServer) {
        wireMockServer.verify(0, postRequestedFor(urlPathMatching("/internal/captcha/.+/checkAndExpire")));
    }

    public static String wiremockFile(String path) {
        try (var inputStream = WiremockUtils.class.getClassLoader()
                .getResourceAsStream("wiremock/__files/" + path)) {
            Objects.requireNonNull(inputStream, "WireMock file not found: " + path);
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void stubCaptchaError(
            WireMockServer wireMockServer,
            UUID captchaId,
            int status,
            String bodyFile
    ) {
        wireMockServer.stubFor(post(urlEqualTo(captchaCheckPath(captchaId)))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile(bodyFile)));
    }

    private static String captchaCheckPath(UUID captchaId) {
        return "/internal/captcha/" + captchaId + "/checkAndExpire";
    }
}
