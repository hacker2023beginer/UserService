package com.study.userservice.service;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import com.study.userservice.exception.BusinessException;
import com.study.userservice.exception.CardException;
import com.study.userservice.exception.UserException;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.impl.PaymentCardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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

    @Test
    void create_userNotFound() {
        PaymentCardDto dto = new PaymentCardDto();
        dto.setUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> cardService.create(dto));
    }

    @Test
    void create_userHasMaxCards() {
        PaymentCardDto dto = new PaymentCardDto();
        dto.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setPaymentCards(Set.of(
                new PaymentCard(), new PaymentCard(), new PaymentCard(),
                new PaymentCard(), new PaymentCard()
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () -> cardService.create(dto));
    }

    @Test
    void getById_success() {
        PaymentCard card = new PaymentCard();
        card.setId(10L);

        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        PaymentCard result = cardService.getById(10L);

        assertEquals(10L, result.getId());
    }

    @Test
    void getById_notFound() {
        when(cardRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(CardException.class, () -> cardService.getById(10L));
    }

    @Test
    void getCardsByUserId_success() {
        when(cardRepository.findByUserId(1L)).thenReturn(List.of());

        List<PaymentCard> result = cardService.getCardsByUserId(1L);

        assertNotNull(result);
        verify(cardRepository).findByUserId(1L);
    }

    @Test
    void update_success() {
        PaymentCardDto dto = new PaymentCardDto();
        dto.setNumber("1234");
        dto.setHolder("Vlad");
        dto.setExpirationDate(LocalDate.now());

        PaymentCard card = new PaymentCard();
        User user = new User();
        user.setId(1L);
        card.setUser(user);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);

        PaymentCard result = cardService.update(1L, dto);

        assertEquals("1234", result.getNumber());
        assertEquals("Vlad", result.getHolder());
        verify(cardRepository).save(card);
    }

    @Test
    void update_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        PaymentCardDto dto = new PaymentCardDto();
        assertThrows(CardException.class, () -> cardService.update(1L, dto));
    }

    @Test
    void activate_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardException.class, () -> cardService.activate(1L));
    }

    @Test
    void deactivate_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardException.class, () -> cardService.deactivate(1L));
    }

    @Test
    void delete_success() {
        PaymentCard card = new PaymentCard();
        User user = new User();
        user.setId(1L);
        card.setUser(user);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.delete(1L);

        verify(cardRepository).delete(card);
    }

    @Test
    void delete_notFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardException.class, () -> cardService.delete(1L));
    }
}