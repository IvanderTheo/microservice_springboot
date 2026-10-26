
package com.example.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PatchPaymentRequest;
import com.example.payment.model.Payment;
import com.example.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;

@OpenAPIDefinition(
    servers = {
        @Server(
            url = "http://localhost:8080",
            description = "API Gateway"
        )
    }
)
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // GET: menampilkan seluruh pembayaran
    @GetMapping
    @Operation(summary = "Menampilkan seluruh pembayaran")
    public List<Payment> index() {
        return paymentService.findAll();
    }

    // GET: menampilkan pembayaran berdasarkan ID
    @GetMapping("/{id}")
    @Operation(summary = "Menampilkan detail pembayaran berdasarkan ID")
    public Payment show(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    // POST: membuat pembayaran baru
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Membuat pembayaran baru")
    public Payment store(
            @Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }

    // PATCH: memperbarui sebagian informasi pembayaran
    @PatchMapping("/{id}")
    @Operation(summary = "Memperbarui sebagian informasi pembayaran")
    public Payment patch(
            @PathVariable Long id,
            @Valid @RequestBody PatchPaymentRequest request) {
        return paymentService.patch(id, request);
    }
}
