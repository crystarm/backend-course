package com.karandashov.model;

import java.util.Objects;

public class Passport implements Document {

    private String id;
    private String series;
    private String number;

    public Passport() {
    }

    public Passport(String id, String series, String number) {
        this.id = id;
        this.series = series;
        this.number = number;
    }

    @Override
    public String getType() {
        return "passport";
    }

    @Override
    public String getId() {
        return id;
    }

    public String getSeries() {
        return series;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Passport) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.series, that.series) &&
                Objects.equals(this.number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, series, number);
    }

    @Override
    public String toString() {
        return "Passport[" +
                "id=" + id + ", " +
                "series=" + series + ", " +
                "number=" + number + ']';
    }
}
