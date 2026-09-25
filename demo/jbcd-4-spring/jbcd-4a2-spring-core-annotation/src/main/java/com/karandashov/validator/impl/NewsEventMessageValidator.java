package com.karandashov.validator.impl;

import com.karandashov.dto.NewsEvent;
import com.karandashov.validator.NewsEventValidator;

public class NewsEventMessageValidator implements NewsEventValidator {

    @Override
    public void validate(NewsEvent event) {

        if (event.getMessage().length() < 10) {
            throw new IllegalArgumentException("Message length must be more than 10 symbols");
        }

        if (event.getMessage().length() > 1000) {
            throw new IllegalArgumentException("Message length must be less than 1000 symbols");
        }
    }
}
