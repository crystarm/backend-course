package com.karandashov;

import com.karandashov.model.UserCsvRecord;
import com.karandashov.model.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(source = "years", target = "age")
    UserCsvRecord toCsvRecord(UserRequest userRequest);
}
