package com.karandashov.parser.impl;

import com.karandashov.model.User;
import com.karandashov.parser.CollectionParser;
import com.karandashov.parser.LineCsvParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class LinkedListCsvParser extends LineCsvParser implements CollectionParser {

    private final String fileName;

    public LinkedListCsvParser(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<User> parseAll() {
        List<User> collection = new LinkedList<>();

        try (
                InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(resourceAsStream));
                BufferedReader br = new BufferedReader(inputStreamReader)
        ) {

            String line = br.readLine();
            while (line != null) {
                User user = parseLine(line);
                if (user != null) {
                    collection.add(user);
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
        return parseAll().getFirst();
    }
}
