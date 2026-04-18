package com.karandashov.jbcd.api.captcha.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsEventList {

    private Collection<NewsEvent> events;
}
