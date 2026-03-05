package com.karandashov.example;

import com.karandashov.repository.ImportantRepository;

public abstract class Example {

    protected final ImportantRepository repository;

    public Example(ImportantRepository repository) {
        this.repository = repository;
    }

    public void start() {
        var thread1 = new Thread(process());
        var thread2 = new Thread(process());
        thread1.start();
        thread2.start();
        // дожидаемся пока закончат
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    abstract Runnable process();
}
