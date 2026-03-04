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

        System.out.println("TOKEN USER ID: " + currentUserId);

        Long dbUserId = userService.getUserByEmail(email).getId();
        System.out.println("DB USER ID: " + dbUserId);

        return dbUserId.equals(currentUserId);
    }
}
