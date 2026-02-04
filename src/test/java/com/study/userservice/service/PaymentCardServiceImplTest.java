package com.study.userservice.service;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import com.study.userservice.exception.BusinessException;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper mapper;

    @InjectMocks
    private PaymentCardServiceImpl cardService;

    @Test
    void createCard_shouldFailIfMoreThan5Cards() {
        User user = new User();
        user.setPaymentCards(Set.of(
                new PaymentCard(), new PaymentCard(),
                new PaymentCard(), new PaymentCard(),
                new PaymentCard()
        ));

        PaymentCardDto dto = new PaymentCardDto(null, null, null, null, null, null);
        dto.setUserId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThrows(BusinessException.class,
                () -> cardService.create(dto));
    }

    @Test
    void activate_shouldSetActiveTrue() {
        PaymentCard card = new PaymentCard();
        card.setActive(false);

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        cardService.activate(1L);

        assertTrue(card.isActive());
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        PaymentCard card = new PaymentCard();
        card.setActive(true);

        when(cardRepository.findById(1L))
                .thenReturn(Optional.of(card));

        cardService.deactivate(1L);

        assertFalse(card.isActive());
    }
}

