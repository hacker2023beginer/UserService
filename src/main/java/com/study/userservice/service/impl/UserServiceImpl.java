package com.study.userservice.service.impl;

import com.study.userservice.dto.UserDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import com.study.userservice.exception.UserException;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.userservice.specification.UserSpecification.hasName;
import static com.study.userservice.specification.UserSpecification.hasSurname;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PaymentCardRepository cardRepository;
    private final int USER_CARDS_MIN = 5;

    public UserServiceImpl(UserRepository userRepository, PaymentCardRepository cardRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public User createUser(User user) {
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public PaymentCard addCardToUser(Long userId, PaymentCard card) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        if (user.getPaymentCards().size() >= USER_CARDS_MIN) {
            throw new UserException("User cannot have more than 5 cards");
        }

        card.setUser(user);
        card.setActive(true);

        user.getPaymentCards().add(card);
        userRepository.save(user);

        return card;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("User not found"));
    }

    @Override
    public List<PaymentCard> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    @Override
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        return userRepository.save(user);
    }

    @Override
    public Page<User> getUsers(String name, String surname, Pageable pageable) {
        Specification<User> spec = Specification.allOf(
                hasName(name),
                hasSurname(surname)
        );
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public User updateUser(Long id, UserDto dto) {
        User user = getUserById(id);

        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setBirthDate(dto.getBirthDate());

        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException("User with id " + id + " not found"));

        userRepository.delete(user);
    }

}
