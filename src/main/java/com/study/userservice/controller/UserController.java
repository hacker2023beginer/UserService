package com.study.userservice.controller;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.dto.UserDto;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.entity.User;
import com.study.userservice.mapper.UserMapper;
import com.study.userservice.service.PaymentCardService;
import com.study.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid UserDto dto
    ) {
        User saved = userService.createUser(userMapper.toEntity(dto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userMapper.toDto(userService.getUserById(id))
        );
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<PaymentCardDto>> getUserCards(@PathVariable Long id) {
        return ResponseEntity.ok(
                paymentCardService.getCardsByUserId(id)
                        .stream()
                        .map(paymentCardMapper::toDto)
                        .toList()
        );
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UserDto dto
    ) {
        return ResponseEntity.ok(
                userMapper.toDto(userService.updateUser(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}



