package com.example.order.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.example.order.dto.CreateOrderItemRequest;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.ProductDetails;
import com.example.order.dto.UpdateOrderRequest;
import com.example.order.dto.UpdateProductStockRequest;
import com.example.order.enums.OrderStatus;
import com.example.order.enums.PaymentStatus;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestClient productClient;
    private final JwtEncoder jwtEncoder;

    public OrderService(
            OrderRepository orderRepository,
            JwtEncoder jwtEncoder,
            @Value("${services.product.base-url}") String productServiceBaseUrl) {
        this.orderRepository = orderRepository;
        this.productClient = RestClient.create(productServiceBaseUrl);
        this.jwtEncoder = jwtEncoder;
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

    @Transactional
    public Order updateItems(Long id, Long userId, String accessToken, UpdateOrderRequest request) {
        Order order = findById(id, userId);
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Paid order cannot be updated");
        }
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one order item is required");
        }

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> updatedItems = new ArrayList<>();
        for (var itemRequest : request.getItems()) {
            if (itemRequest.getProductId() == null || itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each item must include a valid productId and quantity");
            }
            ProductDetails product = findProduct(itemRequest.getProductId(), accessToken);
            BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.id());
            item.setProductName(product.name());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(product.price());
            item.setSubtotal(subtotal);
            updatedItems.add(item);
            total = total.add(subtotal);
        }

        order.getItems().clear();
        order.getItems().addAll(updatedItems);
        order.setTotalAmount(total);
        order.setGrandTotal(total);
        return orderRepository.save(order);
    }

    public Order markPaid(Long id, Long userId) {
        Order order = findById(id, userId);
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is not pending payment");
        }

        reduceProductStock(order);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setOrderStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    public void delete(Long id, Long userId, String accessToken) {
        Order order = findById(id, userId);
        orderRepository.delete(order);
    }

    private void reduceProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            try {
                productClient.patch()
                        .uri("/api/v1/products/{id}/stock", item.getProductId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createOrderServiceToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new UpdateProductStockRequest(item.getQuantity()))
                        .retrieve()
                        .toBodilessEntity();
            } catch (RestClientResponseException exception) {
                if (exception.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Insufficient stock for product " + item.getProductId());
                }
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "Product service rejected stock update for product " + item.getProductId(), exception);
            } catch (ResourceAccessException exception) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Product service is unavailable for stock update", exception);
            }
        }
    }

    private String createOrderServiceToken() {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("user-service")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(300))
                .subject("order-service")
                .claim("user_id", 0L)
                .claim("roles", List.of("ROLE_ADMIN"))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
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
