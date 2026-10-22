package com.example.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePaymentRequest (
    @Size (max = 1000, message = "Notes must not more than 1000")
    String notes,

    @NotNull(message = "Paid Amount is required")
    @DecimalMin (value = "0", message = "Paid Amount Min Value must more than 0")
    BigDecimal paidAmount,

    @NotNull (message = "Payment Date is required")
    LocalDateTime paymentDate,

    @NotNull (message = "Payment Method is required")
    String paymentMethod
){}
