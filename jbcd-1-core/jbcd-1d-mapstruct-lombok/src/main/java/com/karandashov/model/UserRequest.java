package com.karandashov.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRequest {

    private final String firstName;

    private final String lastName;

    private final Integer years;
}
