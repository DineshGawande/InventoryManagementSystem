package com.inventory.repository;

import com.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity
 * Extends JpaRepository for basic CRUD operations
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find product by ID with pessimistic write lock for stock operations
     * This prevents concurrent modifications during stock updates
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") Long id);
    
    /**
     * Find all products that are below their low stock threshold
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();
    
    /**
     * Find products by name (case-insensitive partial match)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Check if a product exists by name
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find products with stock quantity between min and max
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity BETWEEN :min AND :max")
    List<Product> findByStockQuantityBetween(@Param("min") Integer min, @Param("max") Integer max);
    
    /**
     * Get total value of all stock
     */
    @Query("SELECT SUM(p.stockQuantity) FROM Product p")
    Long getTotalStockQuantity();
    
    /**
     * Find out of stock products
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
}