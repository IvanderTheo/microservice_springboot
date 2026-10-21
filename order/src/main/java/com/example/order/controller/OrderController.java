package com.example.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.model.Order;
import com.example.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/api/v1/orders"})
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping 
    public List<Order> index() {
        return orderService.findAll();
    }

    @GetMapping ("/{id}")
    public Order show(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public Order store(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }
}
