package com.karandashov.job;

import com.karandashov.service.TransactionalOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.karandashov.service.TransactionalOutboxService.BATCH_SIZE;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEventsJob {

    private final TransactionalOutboxService service;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "send_events", lockAtLeastFor = "50s", lockAtMostFor = "55s")
    public void finishClassicLots() {
        log.debug("Запускаю задачу на отправку накопленных событий");
        var firstPageRequest = PageRequest.of(0, BATCH_SIZE);
        var page = service.sendEvents(firstPageRequest);
        while (page.isPaged()) {
            page = service.sendEvents(page);
        }
        log.debug("Отправка завершена");
    }
}