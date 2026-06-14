package com.karandashov;

public record User(long id, String name, int age) implements Comparable<User> {

    @Override
    public int compareTo(User other) {
        return Long.compare(id, other.id);
    }
}
