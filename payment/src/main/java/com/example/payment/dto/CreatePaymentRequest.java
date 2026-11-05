package com.example.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest (
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be greater than zero")
    Long orderId,

    @Size (max = 1000, message = "Notes must not more than 1000")
    String notes,

    @NotBlank (message = "Payment Method is required")
    String paymentMethod
){}
