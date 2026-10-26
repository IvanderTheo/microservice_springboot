
package com.example.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PatchPaymentRequest;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // GET: menampilkan seluruh pembayaran
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    // GET: menampilkan pembayaran berdasarkan ID
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() ->
                new RuntimeException("Payment not found")
            );
    }

    // POST: membuat pembayaran baru
    public Payment create(CreatePaymentRequest request) {
        Payment payment = new Payment();

        payment.setNotes(request.notes());
        payment.setPaidAmount(request.paidAmount());
        payment.setPaymentDate(request.paymentDate());
        payment.setPaymentMethod(request.paymentMethod());

        return paymentRepository.save(payment);
    }

    // PATCH: memperbarui sebagian informasi pembayaran
    public Payment patch(Long id, PatchPaymentRequest request) {
        Payment payment = findById(id);

        if (request.notes() != null) {
            payment.setNotes(request.notes());
        }

        if (request.paymentDate() != null) {
            payment.setPaymentDate(request.paymentDate());
        }

        if (request.paymentMethod() != null) {
            payment.setPaymentMethod(request.paymentMethod());
        }

        return paymentRepository.save(payment);
    }
}
