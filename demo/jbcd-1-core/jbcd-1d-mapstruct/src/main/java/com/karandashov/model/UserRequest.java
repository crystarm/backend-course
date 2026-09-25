package com.karandashov.model;

public class UserRequest {

    private final String firstName;

    private final String lastName;

    private final Integer years;

    public UserRequest(String firstName, String lastName, Integer years) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.years = years;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getYears() {
        return years;
    }
}
