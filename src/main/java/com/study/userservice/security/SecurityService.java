package com.study.userservice.security;

import com.study.userservice.repository.PaymentCardRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final PaymentCardRepository cardRepository;

    public SecurityService(PaymentCardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean canAccessCard(Long cardId) {
        Long currentUserId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            return true;
        }

        return cardRepository.findById(cardId)
                .map(card -> card.getUser().getId() == currentUserId)
                .orElse(false);
    }
}
