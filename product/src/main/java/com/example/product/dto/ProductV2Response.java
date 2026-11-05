package com.example.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductV2Response(
        Long id,
        String name,
        String description,
        String category,
        BigDecimal price,
        int stock,
        String imagePath,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
