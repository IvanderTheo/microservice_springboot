package com.example.payment.dto;

import java.math.BigDecimal;

public record OrderDetails(
        Long id,
        Long userId,
        BigDecimal grandTotal,
        String paymentStatus
) {
}
