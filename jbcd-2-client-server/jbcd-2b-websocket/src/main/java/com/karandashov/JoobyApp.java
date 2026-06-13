package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.ErrorResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.jooby.Jooby;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.WebSocket;
import io.jooby.handler.AccessLogHandler;
import io.jooby.jackson.Jackson2Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class JoobyApp extends Jooby {

    private static final Logger log = LoggerFactory.getLogger(JoobyApp.class);

    private static final Map<Session, WebSocket> CLIENTS = new ConcurrentHashMap<>();

    public JoobyApp() {
        // Использование Jackson для JSON
        install(new Jackson2Module());

        // Логирование запросов
        use(new AccessLogHandler());

        // Создаем репозиторий и сервис
        NewsEventRepository repository = new InMemoryNewsEventRepository();
        NewsEventService service = new NewsEventService(repository);

        // Определяем маршруты через Script API
        path("/news", () -> {
            get("/", _ -> service.getAll());

            get("/{id}", ctx -> {
                String id = ctx.path("id").value();
                return service.getById(UUID.fromString(id));
            });

            post("/", ctx -> {
                CreateNewsEventRequest request = ctx.body(CreateNewsEventRequest.class);
                var newsEvent = service.save(request);
                // при добавлении новой записи - асинхронно сообщаем всем открытым каналам
                CompletableFuture.runAsync(() ->
                        CLIENTS.forEach((_, ws) -> ws.send(newsEvent.getId().toString()))
                );
                return newsEvent;
            });

            ws("/ws", (ctx, configurer) -> {
                configurer.onConnect(ws -> {
                    CLIENTS.put(ctx.session(), ws);
                    var response = service.getAll();
                    response.getEvents().forEach(event -> ctx.send(event.getId().toString()));
                });

                configurer.onMessage((_, message) -> log.info("Received message: {}", message));

                configurer.onClose((_, _) -> CLIENTS.remove(ctx.session()));

                configurer.onError((_, cause) -> {
                    CLIENTS.remove(ctx.session());
                    log.error("WebSocket error", cause);
                });
            });
        });

        // Обработка бизнес-исключений
        error(BaseException.class, (ctx, cause, _) -> {
            BaseException e = (BaseException) cause;
            log.error("Business error: {}", e.getMessage());
            ctx.setResponseCode(StatusCode.valueOf(e.getHttpCode()));
            ctx.render(new ErrorResponse(e.getCode(), e.getMessage()));
        });

        // Обработка остальных исключений
        error(Exception.class, (ctx, cause, _) -> {
            log.error("Unexpected error", cause);
            ctx.setResponseCode(StatusCode.SERVER_ERROR);
            ctx.render(new ErrorResponse("internal.server.error", "Internal server error"));
        });
    }
}
