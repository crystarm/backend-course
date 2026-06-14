package com.karandashov.model;

public record Passport(String id, String series, String number) implements Document {

    @Override
    public String type() {
        return "passport";
    }
}
