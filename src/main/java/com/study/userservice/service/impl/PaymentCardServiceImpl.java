package com.study.userservice.service.impl;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.entity.PaymentCard;
import com.study.userservice.entity.User;
import com.study.userservice.exception.BusinessException;
import com.study.userservice.exception.CardException;
import com.study.userservice.exception.UserException;
import com.study.userservice.mapper.PaymentCardMapper;
import com.study.userservice.repository.PaymentCardRepository;
import com.study.userservice.repository.UserRepository;
import com.study.userservice.service.PaymentCardService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository cardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper mapper;
    private static final int USER_CARDS_MAX = 5;

    public PaymentCardServiceImpl(PaymentCardRepository cardRepository,
                                  UserRepository userRepository,
                                  PaymentCardMapper mapper) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public PaymentCard create(PaymentCardDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserException(dto.getUserId().toString()));

        if (user.getPaymentCards().size() >= USER_CARDS_MAX) {
            throw new BusinessException("User cannot have more than 5 cards");
        }

        PaymentCard card = mapper.toEntity(dto);
        card.setUser(user);
        card.setActive(true);

        return cardRepository.save(card);
    }

    @Override
    public PaymentCard getById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardException(id.toString()));
    }

    @Override
    public List<PaymentCard> getCardsByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }


    @Override
    public List<PaymentCard> getByUserId(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public PaymentCard update(Long id, PaymentCardDto dto) {
        PaymentCard card = getById(id);

        card.setNumber(dto.getNumber());
        card.setHolder(dto.getHolder());
        card.setExpirationDate(dto.getExpirationDate());

        return card;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void activate(Long id) {
        PaymentCard card = getById(id);
        card.setActive(true);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deactivate(Long id) {
        PaymentCard card = getById(id);
        card.setActive(false);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void delete(Long id) {
        PaymentCard card = getById(id);
        cardRepository.delete(card);
    }
}
