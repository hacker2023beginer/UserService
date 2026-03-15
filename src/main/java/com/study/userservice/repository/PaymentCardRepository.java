package com.study.userservice.repository;

import com.study.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    List<PaymentCard> findByUserId(Long userId);

    @Query("select c from PaymentCard c where c.user.id = :userId and c.active = true")
    List<PaymentCard> findActiveCardsByUserId(Long userId);

    @Query(
            value = "select * from payment_cards where user_id = :userId",
            nativeQuery = true
    )
    List<PaymentCard> findAllCardsNative(Long userId);
}
