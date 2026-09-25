package com.karandashov;

import java.util.Objects;

public class User implements Comparable<User> {

    private Long id;
    private String name;
    private Integer age;

    public User() {
    }

    public User(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(User other) {
        return Long.compare(id, other.id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return this.id.equals(that.id) &&
                Objects.equals(this.name, that.name) &&
                this.age.equals(that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age);
    }

    @Override
    public String toString() {
        return "User[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "age=" + age + ']';
    }
}
