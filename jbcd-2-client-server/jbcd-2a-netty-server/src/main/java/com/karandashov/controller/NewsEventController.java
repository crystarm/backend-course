package com.karandashov.controller;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.service.NewsEventService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Серверный слой, вызывающий бизнес-логику
 */
public class NewsEventController {

    private static final Pattern ID_PATTERN = Pattern.compile("^/news/([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})$");

    private static final String APPLICATION_JSON = "application/json";

    private final NewsEventService service;

    private final JsonMapper jsonMapper;

    public NewsEventController(NewsEventService service, JsonMapper jsonMapper) {
        this.service = service;
        this.jsonMapper = jsonMapper;
    }

    public FullHttpResponse handleRequest(FullHttpRequest request) {
        try {
            String uri = request.uri();
            HttpMethod method = request.method();

            if (method.equals(HttpMethod.GET)) {
                if (uri.equals("/news")) {
                    return handleGetAll();
                } else {
                    Matcher matcher = ID_PATTERN.matcher(uri);
                    if (matcher.matches()) {
                        String id = matcher.group(1);
                        return handleGetById(UUID.fromString(id));
                    }
                }
            } else if (method.equals(HttpMethod.POST) && uri.equals("/news")) {
                return handleCreate(request);
            }

            return createResponse(HttpResponseStatus.NOT_FOUND,
                    new UniversalResponse<>(404, "Not Found: " + method + " " + uri));
        } catch (BaseException e) {
            e.printStackTrace();
            return createResponse(HttpResponseStatus.valueOf(e.getHttpCode()),
                    new UniversalResponse<>(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    new UniversalResponse<>(500, "Internal Server Error: " + e.getMessage()));
        }
    }

    private FullHttpResponse handleGetById(UUID id) {
        UniversalResponse<?> response = service.getById(id);
        return createResponse(HttpResponseStatus.OK, response);
    }

    private FullHttpResponse handleGetAll() {
        UniversalResponse<?> response = service.getAll();
        return createResponse(HttpResponseStatus.OK, response);
    }

    private FullHttpResponse handleCreate(FullHttpRequest request) {
        String content = request.content().toString(CharsetUtil.UTF_8);
        CreateNewsEventRequest createRequest = jsonMapper.readValue(content, CreateNewsEventRequest.class);
        UniversalResponse<?> response = service.save(createRequest);
        return createResponse(HttpResponseStatus.CREATED, response);
    }

    private FullHttpResponse createResponse(HttpResponseStatus status, Object body) {
        try {
            String json = jsonMapper.writeValueAsString(body);
            ByteBuf content = Unpooled.copiedBuffer(json, CharsetUtil.UTF_8);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, APPLICATION_JSON);
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

            return response;
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
