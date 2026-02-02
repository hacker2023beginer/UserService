package com.study.userservice.controller;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.dto.UserDto;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.entity.User;
import com.study.userservice.mapper.UserMapper;
import com.study.userservice.service.PaymentCardService;
import com.study.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PaymentCardService paymentCardService;
    private final PaymentCardMapper paymentCardMapper;

    public UserController(UserService userService,
                          UserMapper userMapper, PaymentCardService paymentCardService,
                          PaymentCardMapper paymentCardMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.paymentCardService = paymentCardService;
        this.paymentCardMapper = paymentCardMapper;
    }

    @PreAuthorize("#dto.userId == authentication.principal or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid UserDto dto
    ) {
        User saved = userService.createUser(userMapper.toEntity(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDto(saved));
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userMapper.toDto(userService.getUserById(id))
        );
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @GetMapping("/{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getUserCards(@PathVariable Long id) {
        return ResponseEntity.ok(
                paymentCardService.getCardsByUserId(id)
                        .stream()
                        .map(paymentCardMapper::toDto)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            Pageable pageable
    ) {
        Page<User> users = userService.getUsers(name, surname, pageable);
        Page<UserDto> dtoPage = users.map(userMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UserDto dto
    ) {
        return ResponseEntity.ok(
                userMapper.toDto(userService.updateUser(id, dto))
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}



