package com.karandashov.parser;

import com.karandashov.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArrayListCsvParser extends CsvParser {

    private final String fileName;

    public ArrayListCsvParser(String fileName) {
        this.fileName = fileName;
    }

    public List<User> parse() {
        List<User> collection = new ArrayList<>();

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
}
