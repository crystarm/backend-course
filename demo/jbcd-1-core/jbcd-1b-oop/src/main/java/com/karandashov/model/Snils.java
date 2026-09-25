package com.karandashov.model;

import java.util.Objects;

public class Snils implements Document {

    private String id;
    private String number;

    public Snils() {
    }

    public Snils(String id, String number) {
        this.id = id;
        this.number = number;
    }

    @Override
    public String getType() {
        return "snils";
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Snils) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number);
    }

    @Override
    public String toString() {
        return "Snils[" +
                "id=" + id + ", " +
                "number=" + number + ']';
    }
}
