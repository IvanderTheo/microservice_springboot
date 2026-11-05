package com.example.payment.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest (
    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be greater than zero")
    Long orderId,

    @NotNull(message = "Paid amount is required")
    @DecimalMin(value = "0.01", message = "Paid amount must be greater than zero")
    BigDecimal paidAmount,

    @Size (max = 1000, message = "Notes must not more than 1000")
    String notes,

    @NotBlank (message = "Payment Method is required")
    String paymentMethod
){}
