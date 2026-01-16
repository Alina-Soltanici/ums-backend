package com.example.ums.mapper;

/*
When use @Mapper(componentModel = "spring"), MapStruct will generate the implementation of the
mapper interface as a Spring Bean. This means you can inject the mapper into other Spring-managed beans,
such as services and controllers, without manually creating instances of the mapper.
*/


import com.example.ums.dto.RegisterRequest;
import com.example.ums.dto.RegisterResponse;
import com.example.ums.entity.Role;
import com.example.ums.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(RegisterRequest registerRequest);

    @Mapping(target = "message", constant = "Registration completed successfully")
    RegisterResponse toDto(User user);

    // Map a single Role to a String (role name)
    default String map(Role role) {
        return role.getName();
    }

    // Map a Set<Role> to a List<String>
    default List<String> map(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(this::map) // uses the Role → String mapping
                .collect(Collectors.toList());
    }
}
