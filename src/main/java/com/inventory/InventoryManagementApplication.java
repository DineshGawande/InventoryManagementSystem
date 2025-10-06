package com.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Inventory Management System
 */
@SpringBootApplication
@EnableTransactionManagement
public class InventoryManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}