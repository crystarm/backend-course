package com.karandashov.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private final String firstName;

    private final String lastName;

    private Integer age;
}
