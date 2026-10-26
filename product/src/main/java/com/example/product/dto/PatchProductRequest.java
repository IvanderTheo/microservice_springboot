
package com.example.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PatchProductRequest(

    @Size(
        max = 255,
        message = "Name must not exceed 255 characters"
    )
    String name,

    @Size(
        max = 1000,
        message = "Description must not exceed 1000 characters"
    )
    String description,

    @Size(
        max = 255,
        message = "Category must not exceed 255 characters"
    )
    String category,

    @DecimalMin(
        value = "0",
        message = "Price must be greater than or equal to 0"
    )
    BigDecimal price,

    @Min(
        value = 0,
        message = "Stock must be greater than or equal to 0"
    )
    Integer stock

) {}
