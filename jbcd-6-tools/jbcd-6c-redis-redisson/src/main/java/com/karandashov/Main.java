package com.karandashov;

import com.karandashov.example.RedissonLockExample;
import com.karandashov.example.SimpleExample;
import com.karandashov.repository.ImportantRepository;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final int TIMES = 1_000;

    public static void main(String[] args) {

        String redisHost = System.getenv("REDIS_HOST");
        String redisPort = System.getenv("REDIS_PORT");

        if (redisHost == null || redisPort == null) {
            log.error("Не заданы переменные окружения!");
            return;
        }

        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        RedissonClient client = Redisson.create(config);

        /*
        В целом redisson - это клиент с кучей готовых крутых дополнительных надстроек и готовых решений.
        Среди них:
            Распределённые блокировки
            Семафоры и счётчики
            Коллекции с распределённой семантикой
            Кэш с расширенными возможностями (например с уведомлениями)
            ExecutorService для выполнения задач на удаленных нодах (чем-то похоже на grpc)
            и много чего еще.

        Мы рассмотрим использование для распределенной блокировки.
        */
        ImportantRepository importantRepository = new ImportantRepository();

        // плохой пример - два потока работают с одним ресурсом без блокировок
        SimpleExample simpleExample = new SimpleExample(importantRepository);
        simpleExample.start();
        // по идее мы должны получить TIMES * 2
        log.info("Получено: {}", importantRepository.get());
        log.info("Должно было быть: {}", TIMES * 2);
        if (importantRepository.get() != TIMES * 2) {
            log.warn("Получено не ожидаемое значение - гонка данных");
        }
        importantRepository.reset();

        // теперь попробуем решить проблему с помощью блокировок Redisson
        log.info("Запускаем пример с блокировкой от {}", importantRepository.get());
        RedissonLockExample redissonLockExample = new RedissonLockExample(importantRepository, client);
        redissonLockExample.start();
        log.info("Получено: {}", importantRepository.get());
        log.info("Должно было быть: {}", TIMES * 2);
    }
}
