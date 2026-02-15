package com.karandashov.validator.impl;

import com.karandashov.dto.NewsEvent;
import com.karandashov.repository.NewsEventRepository;
import com.karandashov.validator.NewsEventValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NewsEventTitleExistValidator implements NewsEventValidator {

    private final NewsEventRepository repository;

    @Override
    public void validate(NewsEvent event) {

        repository.getAll().forEach(news -> {
            if (news.getTitle().equals(event.getTitle())) {
                throw new IllegalArgumentException("Title must be unique");
            }
        });
    }
}
