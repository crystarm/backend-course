package com.karandashov;

import com.karandashov.model.UserRequest;
import org.mapstruct.factory.Mappers;

public class Main {

    public static void main(String[] args) {

        UserMapper mapper = Mappers.getMapper(UserMapper.class);

        var userRequest = new UserRequest("Иван", "Иваныч", 21);

        var csvRecord = mapper.toCsvRecord(userRequest);

        System.out.println(csvRecord);
    }
}