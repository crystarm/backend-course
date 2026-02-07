package com.karandashov.model;

import java.util.Objects;

public class User {

    private Integer id;

    private String nickname;

    private String firstName;

    private String lastName;

    private String otp;

    private String location;

    public User(Integer id, String nickname, String firstName, String lastName, String otp, String location) {
        this.id = id;
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otp = otp;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(nickname, user.nickname) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(otp, user.otp) && Objects.equals(location, user.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, firstName, lastName, otp, location);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", nickname='" + getNickname() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", otp='" + getOtp() + '\'' +
                ", location='" + getLocation() + '\'' +
                '}';
    }
}
