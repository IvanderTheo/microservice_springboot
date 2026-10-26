
package com.example.payment.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;

public record PatchPaymentRequest(

    @Size(
        max = 1000,
        message = "Notes must not exceed 1000 characters"
    )
    String notes,

    LocalDateTime paymentDate,

    @Size(
        max = 100,
        message = "Payment method must not exceed 100 characters"
    )
    String paymentMethod

) {}
