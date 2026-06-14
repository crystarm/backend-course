package com.karandashov.model;

public record Snils(String id, String number) implements Document {

    @Override
    public String type() {
        return "snils";
    }
}
