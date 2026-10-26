
package com.example.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new RuntimeException("Order Not Found"));
    }

    public Order create(CreateOrderRequest request) {
        Order order = new Order();

        int invoiceNumber = (int) (Math.random() * 10001);

        order.setInvoiceNumber(
                "INVOICE-" + LocalDate.now() + invoiceNumber);

        order.setTransactionDate(LocalDateTime.now());

        order.setTotalAmount(request.getTotalAmount());
        order.setTax(request.getTax());
        order.setDiscount(request.getDiscount());

        validateAmounts(
                order.getTotalAmount(),
                order.getTax(),
                order.getDiscount()
        );

        order.setGrandTotal(calculateGrandTotal(
                order.getTotalAmount(),
                order.getTax(),
                order.getDiscount()
        ));

        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.ONGOING);

        return orderRepository.save(order);
    }

    // PUT: mengganti seluruh data nominal pesanan
    @Transactional
    public Order update(Long id, Order data) {
        Order order = findById(id);

        ensureOrderEditable(order);

        if (data.getTotalAmount() == null
                || data.getTax() == null
                || data.getDiscount() == null) {
            throw new IllegalArgumentException(
                    "totalAmount, tax, and discount are required for PUT"
            );
        }

        validateAmounts(
                data.getTotalAmount(),
                data.getTax(),
                data.getDiscount()
        );

        order.setTotalAmount(data.getTotalAmount());
        order.setTax(data.getTax());
        order.setDiscount(data.getDiscount());

        order.setGrandTotal(calculateGrandTotal(
                data.getTotalAmount(),
                data.getTax(),
                data.getDiscount()
        ));

        return orderRepository.save(order);
    }

    // PATCH: memperbarui sebagian data nominal pesanan
    @Transactional
    public Order patch(Long id, Order data) {
        Order order = findById(id);

        ensureOrderEditable(order);

        BigDecimal totalAmount = data.getTotalAmount() != null
                ? data.getTotalAmount()
                : order.getTotalAmount();

        BigDecimal tax = data.getTax() != null
                ? data.getTax()
                : order.getTax();

        BigDecimal discount = data.getDiscount() != null
                ? data.getDiscount()
                : order.getDiscount();

        validateAmounts(totalAmount, tax, discount);

        order.setTotalAmount(totalAmount);
        order.setTax(tax);
        order.setDiscount(discount);

        order.setGrandTotal(
                calculateGrandTotal(totalAmount, tax, discount)
        );

        return orderRepository.save(order);
    }

    // DELETE: menghapus pesanan yang belum dibayar
    @Transactional
    public void delete(Long id) {
        Order order = findById(id);

        ensureOrderEditable(order);

        orderRepository.delete(order);
    }

    // Memastikan pesanan masih boleh diubah atau dihapus
    private void ensureOrderEditable(Order order) {
        if (order.getPaymentStatus() != PaymentStatus.PENDING
                || order.getOrderStatus() != OrderStatus.ONGOING) {
            throw new IllegalStateException(
                    "Only ongoing orders with pending payment can be modified or deleted"
            );
        }
    }

    // Validasi nilai nominal
    private void validateAmounts(
            BigDecimal totalAmount,
            BigDecimal tax,
            BigDecimal discount) {

        if (totalAmount == null || tax == null || discount == null) {
            throw new IllegalArgumentException(
                    "Total amount, tax, and discount cannot be null"
            );
        }

        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total amount cannot be negative"
            );
        }

        if (tax.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Tax cannot be negative"
            );
        }

        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Discount cannot be negative"
            );
        }

        if (discount.compareTo(totalAmount.add(tax)) > 0) {
            throw new IllegalArgumentException(
                    "Discount cannot exceed total amount plus tax"
            );
        }
    }

    private BigDecimal calculateGrandTotal(
            BigDecimal totalAmount,
            BigDecimal tax,
            BigDecimal discount) {

        return totalAmount.add(tax).subtract(discount);
    }

    // Update payment
    public Order payment(Long id, Order data) {
        Order order = findById(id);

        order.setOrderStatus(OrderStatus.PAID);

        return orderRepository.save(order);
    }
}
