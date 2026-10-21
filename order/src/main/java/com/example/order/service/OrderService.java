package com.example.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.enums.OrderStatus;
import com.example.order.enums.PaymentStatus;
import com.example.order.model.Order;
import com.example.order.repository.OrderRepository;

@Service 
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(()->new RuntimeException("Order Not Found"));
    }

    public Order create(CreateOrderRequest request) {
        Order order = new Order();

        int invoiceNumber = (int) (Math.random() * 10001);
        OrderStatus orderStatus = OrderStatus.Ongoing;
        PaymentStatus paymentStatus = PaymentStatus.Pending;

        order.setInvoiceNumber("INVOICE-"+LocalDate.now()+invoiceNumber);
        order.setTransactionDate(LocalDateTime.now());
        order.setPaymentStatus(paymentStatus);
        order.setOrderStatus(orderStatus);

        return orderRepository.save(order);
    }

    // update payment
    public Order payment(Long id, Order data) {
        Order order = findById(id);

        OrderStatus orderStatus = OrderStatus.Paid;
        order.setOrderStatus(orderStatus);

        return orderRepository.save(order);
    }
}
