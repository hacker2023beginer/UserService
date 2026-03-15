package com.study.userservice.security;

import com.study.userservice.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {

    private final UserService userService;

    public UserSecurity(UserService userService) {
        this.userService = userService;
    }

    public boolean isOwnerByEmail(String email) {
        Long currentUserId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Long dbUserId = userService.getUserByEmail(email).getId();
        return dbUserId.equals(currentUserId);
    }
}
