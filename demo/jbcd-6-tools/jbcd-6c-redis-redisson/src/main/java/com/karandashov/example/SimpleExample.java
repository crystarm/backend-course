package com.karandashov.example;

import com.karandashov.repository.ImportantRepository;

import static com.karandashov.Main.TIMES;

public class SimpleExample extends Example {

    public SimpleExample(ImportantRepository repository) {
        super(repository);
    }

    public Runnable process() {
        return () -> {
            for (int i = 0; i < TIMES; i++) {
                repository.inc();
            }
        };
    }
}
