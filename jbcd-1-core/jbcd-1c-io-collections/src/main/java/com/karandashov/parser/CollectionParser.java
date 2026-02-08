package com.karandashov.parser;

import com.karandashov.model.User;

import java.util.Collection;

public interface CollectionParser extends Parser {

    Collection<User> parseAll();
}
