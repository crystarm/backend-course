package com.karandashov.parser;

import com.karandashov.model.User;

import java.util.Map;

public interface MapParser extends Parser {

    Map<Integer, User> parseAll();
}
