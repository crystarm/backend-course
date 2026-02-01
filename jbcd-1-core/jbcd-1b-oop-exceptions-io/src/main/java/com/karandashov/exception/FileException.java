package com.karandashov.exception;

public class FileException extends RuntimeException {

    public FileException(String fileName, Action action, Exception e) {
        super("Не удалось выполнить действие " + action + " с файлом " + fileName, e);
    }

    // static у enum по-умолчанию, поэтому опускается
    public enum Action {
        READ, WRITE
    }
}
