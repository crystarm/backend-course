package com.karandashov.annotation;

public class Document {

    @Description("Тип документа")
    private final String type;

    private final String value;

    @Description("Номер документа")
    private Integer number;

    public Document(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getDescription() {
        return type + " " + value + " (" + number + ")";
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
