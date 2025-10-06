package com.inventory.exception;

/**
 * Exception thrown when a product is not found in the system
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super(String.format("Product with ID %d not found", id));
    }
    public ProductNotFoundException(String message) {
        super(message);
    }
}
