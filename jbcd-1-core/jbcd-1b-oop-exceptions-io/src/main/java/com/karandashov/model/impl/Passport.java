package com.karandashov.model.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.karandashov.model.Document;
import com.karandashov.model.DocumentType;

import java.time.LocalDate;

public class Passport implements Document {

    private static final DocumentType TYPE = DocumentType.PASSPORT;

    @JsonProperty
    private final Integer series;

    @JsonProperty
    private final Integer number;

    @JsonProperty
    private final String fullName;

    @JsonProperty
    private final LocalDate birthDate;

    public Passport(Integer series, Integer number, String fullName, LocalDate birthDate) {
        this.series = series;
        this.number = number;
        this.fullName = fullName;
        this.birthDate = birthDate;
    }

    /**
     * Пустой конструктор для корректного преобразования в json
     */
    public Passport() {
        this(null, null, null, null);
    }

    @Override
    public DocumentType getType() {
        return TYPE;
    }

    @Override
    public String getId() {
        return series + "-" + number;
    }

    public Integer getSeries() {
        return series;
    }

    public Integer getNumber() {
        return number;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return getType().title + ": " +
                "series=" + getSeries() +
                ", number=" + getNumber() +
                ", fullName='" + getFullName() +
                ", birthDate=" + getBirthDate();
    }
}
