package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.ConnectionFactory;
import com.karandashov.repository.JdbcNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.jooby.Jooby;
import io.jooby.StatusCode;
import io.jooby.handler.AccessLogHandler;
import io.jooby.jackson.JacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class JoobyApp extends Jooby {
    private static final Logger log = LoggerFactory.getLogger(JoobyApp.class);

    public JoobyApp() {
        // Использование Jackson для JSON
        install(new JacksonModule());

        // Логирование запросов
        use(new AccessLogHandler());

        String databaseUrl = System.getenv("DATABASE_URL");
        String databaseUser = System.getenv("DATABASE_USERNAME");
        String databasePassword = System.getenv("DATABASE_PASSWORD");
        String databaseSchema = System.getenv("APP_DATABASE_SCHEMA");

        if (databaseUrl == null || databaseUser == null || databasePassword == null || databaseSchema == null) {
            log.error("Не заданы переменные окружения для подключения к БД!");
            throw new RuntimeException("Не заданы переменные окружения для подключения к БД!");
        }

        // Создаем дерево наших классов, начиная с нижнего слоя
        ConnectionFactory connectionFactory = new ConnectionFactory(databaseUrl, databaseUser, databasePassword);
        NewsEventRepository repository = new JdbcNewsEventRepository(connectionFactory, databaseSchema, "news_table");
        NewsEventService service = new NewsEventService(repository);

        // Определяем маршруты через Script API
        get("/", ctx -> service.getAll());

        get("/{id}", ctx -> {
            String id = ctx.path("id").value();
            return service.getById(UUID.fromString(id));
        });

        post("/", ctx -> {
            CreateNewsEventRequest request = ctx.body(CreateNewsEventRequest.class);
            return service.save(request);
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
