package com.karandashov.util;

import com.karandashov.dto.NotificationEvent;

import java.util.List;

public class EventGenerator {

    private static final List<String> NAMES = List.of("John", "Mary", "Peter", "Anna", "David", "Sarah", "Michael");

    public static NotificationEvent generateEvent() {
        int id = (int) (Math.random() * 100);
        return new NotificationEvent(
                id,
                NAMES.get(id % NAMES.size())
        );
    }
}
