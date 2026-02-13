package com.karandashov;

import com.karandashov.controller.NewsEventController_;
import com.karandashov.dto.UniversalResponse;
import com.karandashov.exceptions.BaseException;
import com.karandashov.repository.InMemoryNewsEventRepository;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.service.NewsEventService;
import io.jooby.Jooby;
import io.jooby.StatusCode;
import io.jooby.handler.AccessLogHandler;
import io.jooby.jackson.JacksonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoobyApp extends Jooby {

    private static final Logger log = LoggerFactory.getLogger(JoobyApp.class);

    public JoobyApp() {
        // Использование Jackson для JSON
        install(new JacksonModule());

        // Логирование запросов
        use(new AccessLogHandler());

        // Создаем репозиторий и сервис
        NewsEventRepository repository = new InMemoryNewsEventRepository();
        NewsEventService service = new NewsEventService(repository);

        mvc(new NewsEventController_(service));

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
