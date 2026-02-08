package com.karandashov.parser;

import com.karandashov.model.User;

public abstract class LineCsvParser {

    protected User parseLine(String line) {
        String[] parts = line.split(";", -1); // сохраняем пустые хвостовые поля
        if (parts.length < 6) {
            // пропуск некорректной строки
            return null;
        }

        Integer id = tryParseInt(parts[0]);
        if (id == null) return null;

        String nickname = normalize(parts[1]);
        String firstName = normalize(parts[2]);
        String lastName = normalize(parts[3]);
        String otp = normalize(parts[4]);
        String location = normalize(parts[5]);

        return new User(id, nickname, firstName, lastName, otp, location);
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalize(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if ("\"\"".equals(v)) {
            return ""; // явное пустое значение
        }
        if (v.isEmpty()) {
            return null; // отсутствует значение
        }
        return v;
    }
}
