package com.example.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "products")
@Setter
@Getter
@NoArgsConstructor

public class Product {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // override price
    public void setPrice(BigDecimal price) {
        if(price != null && price.compareTo(BigDecimal.ZERO)>=0) {
            this.price = price;
        } else {
            throw new IllegalArgumentException("Price harus lebih dari sama dengan 0,00");
        }
    }
    // override stock
    public void setStock(int stock) {
        if(stock >= 0) {
            this.stock = stock;
        } else {
            throw new IllegalArgumentException("Stock harus lebih dari atau sama dengan 0");
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
