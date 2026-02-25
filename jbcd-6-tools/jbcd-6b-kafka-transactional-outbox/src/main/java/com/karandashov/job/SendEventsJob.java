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

    public static final int BATCH_SIZE = 100;

    public static final int BATCH_MAX_ATTEMPTS = 10;

    private final TransactionalOutboxService service;

    /**
     * Достаточно наивная реализация паттерна отправки событий.
     * Она сохраняет порядок по ключу, работает маленькими транзакциями
     * и (за счет shedlock) безопасна для нескольких сервисов.
     * Но при большом количестве событий и медленной отправке может случиться ситуация, когда процесс выходит
     * за lockAtMost и тогда другой сервис рискует нарушить порядок по ключу. В целом решение неплохое, но лучше использовать CDC.
     */
    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "send_events", lockAtLeastFor = "50s", lockAtMostFor = "55s")
    public void finishClassicLots() {
        log.debug("Запускаю задачу на отправку накопленных событий");
        int currentAttempt = 0;
        int processedEventsCount = service.sendEvents(BATCH_SIZE);
        while (processedEventsCount == BATCH_SIZE && currentAttempt < BATCH_MAX_ATTEMPTS) {
            processedEventsCount = service.sendEvents(BATCH_SIZE);
            currentAttempt++;
        }
        log.debug("Отправка завершена");
    }
}