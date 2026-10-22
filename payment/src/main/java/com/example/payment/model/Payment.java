package com.example.payment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table (name = "payments") 
@Setter 
@Getter 
@NoArgsConstructor 
public class Payment {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY) 
    private Long id;

    @Column(nullable = true)
    private String notes;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "payment_date",nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column (name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column (name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void setPaidAmount(BigDecimal paidAmount)  {
        if(paidAmount == null) {
            throw new IllegalArgumentException("Paid Amount cannot be null");
        }

        if(paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.paidAmount = paidAmount;
        } else {
            throw new IllegalArgumentException("Paid Amount must greater than 0");
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
