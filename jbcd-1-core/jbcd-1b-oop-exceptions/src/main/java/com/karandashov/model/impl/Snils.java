package com.karandashov.model.impl;

import com.karandashov.model.Document;
import com.karandashov.model.DocumentType;

public class Snils implements Document {

    private static final DocumentType TYPE = DocumentType.SNILS;

    private final String number;

    public Snils(String number) {
        this.number = number;
    }

    @Override
    public DocumentType getType() {
        return TYPE;
    }

    @Override
    public String getId() {
        return number;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return getType().title + ": " +
                "number='" + getNumber();
    }
}
