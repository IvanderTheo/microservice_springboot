package com.example.product.dto;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String name,
        String description,
        String category,
        BigDecimal price,
        Integer stock
) {
}
