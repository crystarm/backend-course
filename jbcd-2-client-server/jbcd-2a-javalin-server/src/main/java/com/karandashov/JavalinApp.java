package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class JavalinApp {

    private static final Logger log = LoggerFactory.getLogger(JavalinApp.class);

    public Javalin createApp() {
        NewsEventRepository repository = new InMemoryNewsEventRepository();
        NewsEventService service = new NewsEventService(repository);

        return Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson3());
            config.routes.apiBuilder(() -> {
                get("/news", ctx -> ctx.json(service.getAll()));
                get("/news/{id}", ctx -> {
                    String id = ctx.pathParam("id");
                    ctx.json(service.getById(UUID.fromString(id)));
                });
                post("/news", ctx -> {
                    CreateNewsEventRequest request = ctx.bodyAsClass(CreateNewsEventRequest.class);
                    ctx.json(service.save(request));
                });
            });
            config.routes.exception(BaseException.class, (e, ctx) -> {
                log.error("Business error: {}", e.getMessage());
                ctx.status(e.getHttpCode());
                ctx.json(new UniversalResponse<>(e.getCode(), e.getMessage()));
            });
            config.routes.exception(Exception.class, (e, ctx) -> {
                log.error("Unexpected error", e);
                ctx.status(500);
                ctx.json(new UniversalResponse<>(5000, "Неизвестная ошибка: " + e.getMessage()));
            });
        });
    }
}
