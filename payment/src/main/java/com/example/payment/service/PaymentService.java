package com.example.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;

@Service 
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Payment Not Fount"));
    }
    public Payment create(CreatePaymentRequest request) {
        Payment payment = new Payment();

        payment.setNotes(request.notes());
        payment.setPaidAmount(request.paidAmount());
        payment.setPaymentDate(request.paymentDate());
        payment.setPaymentMethod(request.paymentMethod());

        return paymentRepository.save(payment);
    }
}
