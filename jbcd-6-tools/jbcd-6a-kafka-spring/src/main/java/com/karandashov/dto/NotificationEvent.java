package com.karandashov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    public static final String TOPIC = "NOTIFICATIONS";

    private int id;

    private String user;
}
