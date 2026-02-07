package com.karandashov;

import com.karandashov.model.User;

public class Main {

    public static void main(String[] args) {

        User user1 = new User("Иван", "Иваныч");
        user1.setAge(21);
        System.out.println(user1.getFirstName());
        System.out.println(user1.getLastName());

        User user2 = new User("Петр", "Петрович", 21);
        System.out.println(user2.getAge());
    }
}