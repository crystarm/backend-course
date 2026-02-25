package com.karandashov.job;

import com.karandashov.service.TransactionalOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEventsJob {

    private final TransactionalOutboxService service;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "send_events", lockAtLeastFor = "50s", lockAtMostFor = "55s")
    public void finishClassicLots() {
        log.debug("Запускаю задачу на отправку накопленных событий");
        service.sendEvents();
        log.debug("Отправка завершена");
    }
}