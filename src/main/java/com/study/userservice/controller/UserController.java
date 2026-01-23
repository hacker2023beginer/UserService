package com.study.userservice.controller;

import com.study.userservice.dto.UserDto;
import com.study.userservice.mapper.UserMapper;
import com.study.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto dto) {
        return userMapper.toDto(
                userService.createUser(
                        userMapper.toEntity(dto)
                )
        );
    }
}


