package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.jooby.Jooby;
import io.jooby.ServerSentEmitter;
import io.jooby.StatusCode;
import io.jooby.handler.AccessLogHandler;
import io.jooby.jackson.Jackson2Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class JoobyApp extends Jooby {

    private static final Logger log = LoggerFactory.getLogger(JoobyApp.class);

    private static final Set<ServerSentEmitter> CLIENTS = ConcurrentHashMap.newKeySet();

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
            get("/", ctx -> service.getAll());

            get("/{id}", ctx -> {
                String id = ctx.path("id").value();
                return service.getById(UUID.fromString(id));
            });

            post("/", ctx -> {
                CreateNewsEventRequest request = ctx.body(CreateNewsEventRequest.class);
                var newsEvent = service.save(request);
                // при добавлении новой записи - асинхронно сообщаем всем открытым sse-каналам
                CompletableFuture.runAsync(() ->
                        CLIENTS.forEach(client -> client.send(newsEvent.getData()))
                );
                return newsEvent;
            });

            sse("/listen", client -> {
                client.keepAlive(30, TimeUnit.SECONDS);
                client.onClose(() -> CLIENTS.remove(client));
                CLIENTS.add(client);

                // отправляем существующие записи в канал
                var existEvents = service.getAll().getData().getEvents();
                existEvents.forEach(client::send);
            });
        });

        // Обработка бизнес-исключений
        error(BaseException.class, (ctx, cause, statusCode) -> {
            BaseException e = (BaseException) cause;
            log.error("Business error: {}", e.getMessage());
            ctx.setResponseCode(StatusCode.valueOf(e.getHttpCode()));
            ctx.render(new UniversalResponse<>(e.getCode(), e.getMessage()));
        });

        // Обработка остальных исключений
        error(Exception.class, (ctx, cause, statusCode) -> {
            log.error("Unexpected error", cause);
            ctx.setResponseCode(StatusCode.SERVER_ERROR);
            ctx.render(new UniversalResponse<>(5000, "Неизвестная ошибка: " + cause.getMessage()));
        });
    }
}
