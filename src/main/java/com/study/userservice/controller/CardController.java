package com.study.userservice.controller;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final PaymentCardService cardService;
    private final PaymentCardMapper mapper;

    public CardController(PaymentCardService cardService,
                          PaymentCardMapper mapper) {
        this.cardService = cardService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<PaymentCardDto> create(
            @RequestBody @Valid PaymentCardDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDto(cardService.create(dto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toDto(cardService.getById(id))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

