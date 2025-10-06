package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Product entity representing items in the inventory system.
 * This class maps to the 'products' table in the database.
 */
@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 10;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Version
    private Long version; // For optimistic locking
    
    /**
     * Business method to check if product is low on stock
     */
    public boolean isLowStock() {
        return this.stockQuantity <= this.lowStockThreshold;
    }
    
    /**
     * Business method to add stock
     */
    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be positive");
        }
        this.stockQuantity += quantity;
    }
    
    /**
     * Business method to remove stock
     */
    public void removeStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to remove must be positive");
        }
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException(
                String.format("Insufficient stock. Available: %d, Requested: %d", 
                    this.stockQuantity, quantity)
            );
        }
        this.stockQuantity -= quantity;
    }
    
    /**
     * Pre-persist validation
     */
    @PrePersist
    @PreUpdate
    public void validateStock() {
        if (this.stockQuantity < 0) {
            throw new IllegalStateException("Stock quantity cannot be negative");
        }
        if (this.lowStockThreshold < 0) {
            throw new IllegalStateException("Low stock threshold cannot be negative");
        }
    }
}