package com.study.userservice.mapper;

import com.study.userservice.dto.UserDto;
import com.study.userservice.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User entity);
}
