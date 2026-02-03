package com.study.userservice.service;

import com.study.userservice.dto.UserDto;
import com.study.userservice.entity.User;
import com.study.userservice.exception.UserException;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardRepository cardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldSetActiveTrue() {
        User user = new User();
        user.setName("Ivan");

        when(userRepository.save(any(User.class)))
                .thenAnswer((InvocationOnMock i) -> i.getArgument(0));

        User saved = userService.createUser(user);

        assertTrue(saved.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_whenNotFound_shouldThrowException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UserException.class,
                () -> userService.getUserById(1L));
    }

    @Test
    void activateUser_shouldSetActiveTrue() {
        User user = new User();
        user.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);

        User result = userService.activateUser(1L);

        assertTrue(result.isActive());
    }

    @Test
    void deactivateUser_shouldSetActiveFalse() {
        User user = new User();
        user.setActive(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);

        User result = userService.deactivateUser(1L);

        assertFalse(result.isActive());
    }

    @Test
    void deleteUser_shouldCallRepositoryDelete() {
        User user = new User();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void getUserById_success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getCardsByUserId_success() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of());

        List<?> result = userService.getCardsByUserId(1L);

        assertNotNull(result);
        verify(cardRepository).findByUserId(1L);
    }

    @Test
    void updateUser_success() {
        User existing = new User();
        existing.setId(1L);

        UserDto dto = new UserDto();
        dto.setName("NewName");
        dto.setSurname("NewSurname");
        dto.setBirthDate(LocalDate.now());
        dto.setEmail("new@mail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        User result = userService.updateUser(1L, dto);

        assertEquals("NewName", result.getName());
        assertEquals("NewSurname", result.getSurname());
        assertEquals("new@mail.com", result.getEmail());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto userDto = new UserDto();
        assertThrows(UserException.class, () -> userService.updateUser(1L, userDto));
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserWithCards_success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findUserWithCards(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserWithCards(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserWithCards_notFound() {
        when(userRepository.findUserWithCards(1L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> userService.getUserWithCards(1L));
    }
}

