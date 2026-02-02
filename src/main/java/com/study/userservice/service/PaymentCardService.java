package com.study.userservice.service;

import com.study.userservice.dto.PaymentCardDto;
import com.study.userservice.entity.PaymentCard;
import java.util.List;

public interface PaymentCardService {

    PaymentCard create(PaymentCardDto dto);

    PaymentCard getById(Long id);

    PaymentCard update(Long id, PaymentCardDto dto);

    void activate(Long id);

    void deactivate(Long id);

    void delete(Long id);

    List<PaymentCard> getCardsByUserId(Long userId);
}
