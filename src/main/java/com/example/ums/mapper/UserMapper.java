package com.example.ums.mapper;

/*
When use @Mapper(componentModel = "spring"), MapStruct will generate the implementation of the
mapper interface as a Spring Bean. This means you can inject the mapper into other Spring-managed beans,
such as services and controllers, without manually creating instances of the mapper.
*/


import com.example.ums.dto.RegisterRequest;
import com.example.ums.dto.RegisterResponse;
import com.example.ums.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    //mapping for signup feature
    User toEntity(RegisterRequest registerRequest);
    @Mapping(target = "message", constant = "registration completed successfully")
    RegisterResponse toDto(User user);
}
