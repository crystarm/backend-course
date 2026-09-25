package com.karandashov.model;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private final String firstName;

    private final String lastName;

    private Integer age;
}
