package com.example.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.model.Order;
import com.example.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> index(@AuthenticationPrincipal Jwt jwt) {
        return orderService.findAll(userId(jwt));
    }

    @GetMapping("/{id}")
    public Order show(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return orderService.findById(id, userId(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order store(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return orderService.create(request, userId(jwt), jwt.getTokenValue());
    }

    @PatchMapping("/{id}/payment")
    public Order markPaid(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return orderService.markPaid(id, jwt.getClaim("user_id"));
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
