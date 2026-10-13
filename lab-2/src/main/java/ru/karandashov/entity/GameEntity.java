package ru.karandashov.entity;

import java.util.Objects;

public final class GameEntity {

    private static final int MAX_TITLE_LENGTH = 255;

    private final Integer id;
    private final String title;
    private final double score;

    public GameEntity(Integer id, String title, double score) {
        this.id = id;
        this.title = validateTitle(title);
        if (!Double.isFinite(score)) {
            throw new IllegalArgumentException("Оценка игры должна быть конечным числом");
        }
        this.score = score;
    }

    public GameEntity(String title, double score) {
        this(null, title, score);
    }

    private static String validateTitle(String title) {
        Objects.requireNonNull(title, "Название игры не может быть null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("Название игры не может быть пустым");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("Название игры не может быть длиннее " + MAX_TITLE_LENGTH + " символов");
        }
        return title;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof GameEntity game)) {
            return false;
        }
        return Double.compare(score, game.score) == 0
                && Objects.equals(id, game.id)
                && title.equals(game.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, score);
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", score=" + score +
                '}';
    }
}
