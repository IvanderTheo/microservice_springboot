package com.example.order.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.order.enums.OrderStatus;
import com.example.order.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "orders")
@Getter 
@Setter 
@NoArgsConstructor
public class Order {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number",nullable = false)
    private String invoiceNumber;

    @Column(name = "transaction_date",nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "total_amount",nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "tax",nullable = false, precision = 12, scale = 2)
    private BigDecimal tax;

    @Column(name = "discount",nullable = false, precision = 2, scale = 2)
    private BigDecimal discount;

    @Column(name = "grand_total",nullable = false, precision = 12, scale = 2)
    private BigDecimal grandTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status",nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void setTransactionDate(LocalDateTime transactionDate) {
        if (transactionDate == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }

        // Bandingkan tanggalnya saja (tanpa jam/menit/detik)
        LocalDate today = LocalDate.now();
        LocalDate inputDate = transactionDate.toLocalDate();

        if (inputDate.isEqual(today)) {
            this.transactionDate = transactionDate;
        } else {
            throw new IllegalArgumentException("Transaction date must be today!");
        }
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        if(totalAmount == null) {
            throw new IllegalArgumentException("Total Amount cannot be null");
        }
        if(totalAmount.compareTo(BigDecimal.ZERO) >= 0) {
            this.totalAmount = totalAmount;
        } else {
            throw new IllegalArgumentException("Total Amount must more than 0");
        }
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        if(grandTotal == null) {
            throw new IllegalArgumentException("Grand Total cannot be null");
        }
        if(grandTotal.compareTo(BigDecimal.ZERO) >= 0) {
            this.grandTotal = grandTotal;
        } else {
            throw new IllegalArgumentException("Grand Total must more than 0");
        }
    }

    @PrePersist 
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate 
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
