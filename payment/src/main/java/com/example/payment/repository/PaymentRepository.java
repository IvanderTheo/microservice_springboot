package com.example.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.payment.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long>{
    List<Payment> findAllByUserId(Long userId);

    Optional<Payment> findByIdAndUserId(Long id, Long userId);

    boolean existsByOrderId(Long orderId);
}
