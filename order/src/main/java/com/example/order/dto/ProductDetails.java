package com.example.order.dto;

import java.math.BigDecimal;

public record ProductDetails(
        Long id,
        String name,
        BigDecimal price,
        int stock
) {
}
