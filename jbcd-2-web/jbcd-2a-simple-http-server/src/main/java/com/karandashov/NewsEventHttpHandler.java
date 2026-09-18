package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.ErrorResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.service.NewsEventService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Пример HTTP-обработчика без web-фреймворка.
 * HttpServer передает сюда необработанный {@link HttpExchange}.
 * Поэтому маршрутизацию, чтение тела запроса и формирование ответа нужно сделать самостоятельно
 */
public class NewsEventHttpHandler implements HttpHandler {

    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";
    private static final Pattern NEWS_BY_ID = Pattern.compile(
            "^/news/([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})$");

    private final NewsEventService service;
    private final JsonMapper jsonMapper;

    public NewsEventHttpHandler(JsonMapper jsonMapper) {
        this(new NewsEventService(new InMemoryNewsEventRepository()), jsonMapper);
    }

    public NewsEventHttpHandler(NewsEventService service, JsonMapper jsonMapper) {
        this.service = service;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            try {
                handleRequest(exchange);
            } catch (BaseException e) {
                sendJson(exchange, e.getHttpCode(), new ErrorResponse(e.getCode(), e.getMessage()));
            } catch (JacksonException | IllegalArgumentException e) {
                sendJson(exchange, 400, new ErrorResponse("bad.request", "Invalid request"));
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500,
                        new ErrorResponse("internal.server.error", "Internal server error"));
            }
        }
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method) && "/news".equals(path)) {
            sendJson(exchange, 200, service.getAll());
            return;
        }

        Matcher matcher = NEWS_BY_ID.matcher(path);
        if ("GET".equals(method) && matcher.matches()) {
            sendJson(exchange, 200, service.getById(UUID.fromString(matcher.group(1))));
            return;
        }

        if ("POST".equals(method) && "/news".equals(path)) {
            CreateNewsEventRequest request = readJson(exchange, CreateNewsEventRequest.class);
            sendJson(exchange, 201, service.save(request));
            return;
        }

        sendJson(exchange, 404, new ErrorResponse("not.found", "Not found"));
    }

    private <T> T readJson(HttpExchange exchange, Class<T> type) throws IOException {
        try (InputStream body = exchange.getRequestBody()) {
            String json = new String(body.readAllBytes(), StandardCharsets.UTF_8);
            return jsonMapper.readValue(json, type);
        }
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] responseBody = jsonMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", APPLICATION_JSON);
        exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBody.length));
        exchange.sendResponseHeaders(statusCode, responseBody.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseBody);
        }
    }
}
