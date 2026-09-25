package com.karandashov.example;

import com.karandashov.repository.ImportantRepository;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedissonLockExample extends Example {

    private final RedissonClient client;

    public RedissonLockExample(ImportantRepository repository, RedissonClient client) {
        super(repository);
        this.client = client;
    }

    @Override
    Runnable process() {
        return () -> {
            for (int i = 0; i < 1000; i++) {
                var lock = client.getLock("lock");
                boolean acquired = false;
                try {
                    acquired = lock.tryLock(5, 30, TimeUnit.SECONDS);
                    if (!acquired) {
                        continue;
                    }
                    repository.inc();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    if (acquired && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        };
    }
}
