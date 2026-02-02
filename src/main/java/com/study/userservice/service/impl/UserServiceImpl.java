package com.study.userservice.service.impl;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.dto.UserDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import com.study.userservice.exception.UserException;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.PaymentCardService;
import com.study.userservice.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.study.userservice.specification.UserSpecification.hasName;
import static com.study.userservice.specification.UserSpecification.hasSurname;

@Service
public class UserServiceImpl implements UserService {
    private final PaymentCardMapper mapper;
    private final UserRepository userRepository;
    private final PaymentCardRepository cardRepository;
    private final PaymentCardService paymentCardService;
    private static final int USER_CARDS_MAX = 5;

    public UserServiceImpl(PaymentCardMapper mapper, UserRepository userRepository, PaymentCardRepository cardRepository, PaymentCardService paymentCardService) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.paymentCardService = paymentCardService;
    }

    @Override
    public User createUser(User user) {
        user.setActive(true);
        return userRepository.save(user);
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
    @CachePut(value = "users", key = "#id")
    @Transactional
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
        return userRepository.save(user);
    }

    @Override
    @CachePut(value = "users", key = "#id")
    @Transactional
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
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

    @Override
    @CachePut(value = "users", key = "#id")
    @Transactional
    public User updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(id.toString()));

        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        userRepository.save(user);
        return user;
    }

    @Override
    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(id.toString()));
        userRepository.delete(user);
    }

    @Override
    @Cacheable(value = "users", key = "#userId")
    public User getUserWithCards(Long userId) {
        return userRepository.findUserWithCards(userId)
                .orElseThrow(() -> new UserException(userId.toString()));
    }

}
