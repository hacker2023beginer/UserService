package com.study.userservice.repository;

import com.study.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.paymentCards WHERE u.id = :userId")
    Optional<User> findUserWithCards(Long userId);
}
