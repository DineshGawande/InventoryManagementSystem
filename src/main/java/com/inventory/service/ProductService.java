package com.inventory.service;

import com.inventory.dto.*;
import java.util.List;

/**
 * Service interface for Product operations
 */
public interface ProductService {
    
    /**
     * Get all products
     */
    List<ProductDTO> getAllProducts();
    
    /**
     * Get product by ID
     */
    ProductDTO getProductById(Long id);
    
    /**
     * Create a new product
     */
    ProductDTO createProduct(ProductCreateDTO createDTO);
    
    /**
     * Update product information
     */
    ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO);
    
    /**
     * Delete a product
     */
    void deleteProduct(Long id);
    
    /**
     * Add stock to a product
     */
    ProductDTO addStock(Long productId, StockUpdateDTO stockUpdateDTO);
    
    /**
     * Remove stock from a product
     */
    ProductDTO removeStock(Long productId, StockUpdateDTO stockUpdateDTO);
    
    /**
     * Get all products with low stock
     */
    List<ProductDTO> getLowStockProducts();
    
    /**
     * Search products by name
     */
    List<ProductDTO> searchProductsByName(String name);
}

// File: ProductServiceImpl.java
