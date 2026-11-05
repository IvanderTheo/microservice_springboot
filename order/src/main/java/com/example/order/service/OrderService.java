package com.example.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.example.order.dto.CreateOrderItemRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.ProductDetails;
import com.example.order.enums.OrderStatus;
import com.example.order.enums.PaymentStatus;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestClient productClient;

    public OrderService(
            OrderRepository orderRepository,
            @Value("${services.product.base-url}") String productServiceBaseUrl) {
        this.orderRepository = orderRepository;
        this.productClient = RestClient.create(productServiceBaseUrl);
    }

    public List<Order> findAll(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    public Order findById(Long id, Long userId) {
        return orderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    public Order create(CreateOrderRequest request, Long userId, String accessToken) {
        Map<Long, Integer> requestedQuantities = new HashMap<>();
        for (CreateOrderItemRequest item : request.getItems()) {
            requestedQuantities.merge(item.productId(), item.quantity(), Math::addExact);
        }

        Map<Long, ProductDetails> products = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : requestedQuantities.entrySet()) {
            ProductDetails product = findProduct(entry.getKey(), accessToken);
            if (product.stock() < entry.getValue()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Insufficient stock for product " + product.id());
            }
            products.put(product.id(), product);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setInvoiceNumber("INVOICE-" + UUID.randomUUID());
        order.setTransactionDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.ONGOING);
        order.setTax(BigDecimal.ZERO);
        order.setDiscount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderItemRequest requestedItem : request.getItems()) {
            ProductDetails product = products.get(requestedItem.productId());
            BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(requestedItem.quantity()));
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.id());
            item.setProductName(product.name());
            item.setQuantity(requestedItem.quantity());
            item.setUnitPrice(product.price());
            item.setSubtotal(subtotal);
            orderItems.add(item);
            total = total.add(subtotal);
        }

        order.setItems(orderItems);
        order.setTotalAmount(total);
        order.setGrandTotal(total);
        return orderRepository.save(order);
    }

    public Order markPaid(Long id, Long userId) {
        Order order = findById(id, userId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is not pending payment");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    private ProductDetails findProduct(Long productId, String accessToken) {
        try {
            ProductDetails product = productClient.get()
                    .uri("/api/v1/products/{id}", productId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(ProductDetails.class);
            if (product == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product service returned an empty response");
            }
            return product;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productId + " does not exist");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Product service request failed", exception);
        } catch (ResourceAccessException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Product service is unavailable", exception);
        }
    }
}
