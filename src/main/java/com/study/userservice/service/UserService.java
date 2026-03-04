package com.study.userservice.service;

import com.study.userservice.dto.UserDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<PaymentCard> getCardsByUserId(Long userId);

    User activateUser(Long id);

    User deactivateUser(Long id);

    Page<User> getUsers(String name, String surname, Pageable pageable);

    User updateUser(Long id, UserDto dto);

    void deleteUser(Long id);

    User getUserWithCards(Long userId);

    Boolean validate(Long id, String email);
}
