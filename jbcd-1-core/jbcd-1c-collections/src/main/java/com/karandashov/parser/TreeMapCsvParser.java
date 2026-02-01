package com.karandashov.parser;

import com.karandashov.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

public class TreeMapCsvParser extends CsvParser {

    private final String fileName;

    public TreeMapCsvParser(String fileName) {
        this.fileName = fileName;
    }

    public SortedMap<Integer, User> parse() {
        SortedMap<Integer, User> collection = new TreeMap<>();

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
}
