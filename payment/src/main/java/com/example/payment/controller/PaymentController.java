package com.example.payment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.model.Payment;
import com.example.payment.service.PaymentService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController 
@RequestMapping ({"/api/v1/payment"})
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping()
    public List<Payment> index() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public Payment show(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping()
    @ResponseStatus (HttpStatus.CREATED)
    public Payment store(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.create(request);
    }
}
