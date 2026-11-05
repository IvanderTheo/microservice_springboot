package com.example.payment.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.OrderDetails;
import com.example.payment.model.Payment;
import com.example.payment.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestClient orderClient;
    private final JwtEncoder jwtEncoder;

    public PaymentService(
            PaymentRepository paymentRepository,
            JwtEncoder jwtEncoder,
            @Value("${services.order.base-url}") String orderServiceBaseUrl) {
        this.paymentRepository = paymentRepository;
        this.jwtEncoder = jwtEncoder;
        this.orderClient = RestClient.create(orderServiceBaseUrl);
    }

    public List<Payment> findAll(Long userId) {
        return paymentRepository.findAllByUserId(userId);
    }

    public Payment findById(Long id, Long userId) {
        return paymentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    public void delete(Long id, Long userId) {
        Payment payment = findById(id, userId);
        paymentRepository.delete(payment);
    }

    @Transactional
    public Payment create(CreatePaymentRequest request, Long userId, String accessToken) {
        OrderDetails order = findOrder(request.orderId(), accessToken);
        if (!userId.equals(order.userId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        if (!"PENDING".equalsIgnoreCase(order.paymentStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is not pending payment");
        }
        if (paymentRepository.existsByOrderId(order.id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order already has a payment");
        }
        if (request.paidAmount().compareTo(order.grandTotal()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "saldo kurang");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.id());
        payment.setUserId(userId);
        payment.setNotes(request.notes());
        payment.setPaidAmount(request.paidAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(request.paymentMethod());

        Payment savedPayment = paymentRepository.save(payment);
        confirmOrderPayment(order.id(), userId);
        return savedPayment;
    }

    private OrderDetails findOrder(Long orderId, String accessToken) {
        try {
            OrderDetails order = orderClient.get()
                    .uri("/api/v1/orders/{id}", orderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(OrderDetails.class);
            if (order == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Order service returned an empty response");
            }
            return order;
        } catch (RestClientResponseException exception) {
            throw new ResponseStatusException(
                    exception.getStatusCode(), "Order service rejected the request", exception);
        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Order service is unavailable", exception);
        }
    }

    private void confirmOrderPayment(Long orderId, Long userId) {
        try {
            orderClient.patch()
                    .uri("/api/v1/orders/{id}/payment", orderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + createPaymentServiceToken(userId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            throw new ResponseStatusException(
                    exception.getStatusCode(), "Order service rejected payment confirmation", exception);
        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Could not confirm payment with order service", exception);
        }
    }

    private String createPaymentServiceToken(Long userId) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("user-service")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(60))
                .subject("payment-service")
                .claim("user_id", userId)
                .claim("roles", List.of("ROLE_PAYMENT_SERVICE"))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
