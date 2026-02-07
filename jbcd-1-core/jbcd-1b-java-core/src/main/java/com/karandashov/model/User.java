package com.karandashov.model;

public class User {

    private final String firstName;

    private final String lastName;

    private Integer age;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName + " (" + age + ")";
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
