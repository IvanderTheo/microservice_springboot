package com.example.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(

    @NotBlank (message = "Name is required")
    @Size (max = 255, message = "Name must not exceed 255 characters")
    String name,

    @NotNull (message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotNull(message = "Category is required")
    @Size(max = 255, message = "Category must not exceed 255 characters")
    String category,

    @NotNull (message = "Price is required")
    @DecimalMin (value = "0", message = "Price must be greater than or equal 0")
    BigDecimal price,

    @NotNull (message = "Stock is required")
    @Min(value=0, message="Stock must be greater than or equal 0")
    Integer stock

) {}
