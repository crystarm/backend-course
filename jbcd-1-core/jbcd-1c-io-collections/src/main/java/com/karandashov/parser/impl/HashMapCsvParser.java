package com.karandashov.parser.impl;

import com.karandashov.model.User;
import com.karandashov.parser.LineCsvParser;
import com.karandashov.parser.MapParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashMapCsvParser extends LineCsvParser implements MapParser {

    private final String fileName;

    public HashMapCsvParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<Integer, User> parseAll() {

        Map<Integer, User> collection = new HashMap<>();

        try (
                InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(resourceAsStream));
                BufferedReader br = new BufferedReader(inputStreamReader)
        ) {

            String line = br.readLine();
            while (line != null) {
                User user = parseLine(line);
                if (user != null) {
                    collection.put(user.getId(), user);
                }
                line = br.readLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Возникла проблема при работе с файлом: " + fileName, e);
        }

        return collection;
    }

    @Override
    public User parse() {
        return parseAll().values().iterator().next();
    }
}
