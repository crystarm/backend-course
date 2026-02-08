package com.karandashov.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCsvRecord {

    private String firstName;

    private String lastName;

    private Integer age;
}
