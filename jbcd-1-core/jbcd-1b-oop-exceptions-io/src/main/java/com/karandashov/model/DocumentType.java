package com.karandashov.model;

/**
 * Тип документа
 */
public enum DocumentType {

    PASSPORT("Паспорт"),
    SNILS("СНИЛС")
    ;

    public final String title;

    // конструктор enum всегда private, поэтому private опускается
    DocumentType(String title) {
        this.title = title;
    }
}
