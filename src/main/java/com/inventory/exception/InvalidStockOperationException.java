package com.inventory.exception;

/**
 * Exception for invalid stock operations
 */
public class InvalidStockOperationException extends RuntimeException {
    public InvalidStockOperationException(String message) {
        super(message);
    }
}
