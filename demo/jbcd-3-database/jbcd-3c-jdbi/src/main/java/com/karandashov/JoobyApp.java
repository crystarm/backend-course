package com.karandashov;

import com.karandashov.dto.CreateNewsEventRequest;
import com.karandashov.dto.ErrorResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.ConnectionFactory;
import com.karandashov.repository.DatabaseMigrator;
import com.karandashov.repository.JdbcNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.repository.PropertiesService;
import com.karandashov.service.NewsEventService;
import io.jooby.Jooby;
import io.jooby.StatusCode;
import io.jooby.handler.AccessLogHandler;
import io.jooby.jackson.Jackson2Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class JoobyApp extends Jooby {
    private static final Logger log = LoggerFactory.getLogger(JoobyApp.class);

    public JoobyApp() {
        // Использование Jackson для JSON
        install(new Jackson2Module());

        // Логирование запросов
        use(new AccessLogHandler());

        PropertiesService propertiesService = new PropertiesService();
        String databaseUrl = propertiesService.get("database.url");
        String databaseUser = propertiesService.get("database.username");
        String databasePassword = propertiesService.get("database.password");
        String databaseSchema = propertiesService.get("database.schema");

        // Создаем дерево наших классов, начиная с нижнего слоя
        ConnectionFactory connectionFactory = new ConnectionFactory(databaseUrl, databaseUser, databasePassword, databaseSchema);
        NewsEventRepository repository = new JdbcNewsEventRepository(connectionFactory.getDataSource());
        NewsEventService service = new NewsEventService(repository);

        DatabaseMigrator databaseMigrator = new DatabaseMigrator(databaseUrl, databaseUser, databasePassword, databaseSchema);
        databaseMigrator.runMigrations();

        // Определяем маршруты через Script API
        get("/", _ -> service.getAll());

        get("/{id}", ctx -> {
            String id = ctx.path("id").value();
            return service.getById(UUID.fromString(id));
        });

        post("/", ctx -> {
            CreateNewsEventRequest request = ctx.body(CreateNewsEventRequest.class);
            return service.save(request);
        });

        // Обработка бизнес-исключений
        error(BaseException.class, (ctx, cause, _) -> {
            log.error("Business error: ", cause);
            BaseException e = (BaseException) cause;
            ctx.setResponseCode(StatusCode.valueOf(e.getHttpCode()));
            ctx.render(new ErrorResponse(e.getCode(), e.getMessage()));
        });

        // Обработка остальных исключений
        error(Exception.class, (ctx, cause, _) -> {
            log.error("Unexpected error: ", cause);
            ctx.setResponseCode(StatusCode.SERVER_ERROR);
            ctx.render(new ErrorResponse("internal.server.error", "Internal server error"));
        });
    }
}
