package com.karandashov.model;

/**
 * Интерфейс-маркер, которым помечаются DTO (data transfer object) представляющие документы
 */
public interface Document extends Entity {

    DocumentType getType();
}
