package com.inventory.controller;

import com.inventory.dto.*;
import com.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product management
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "Endpoints for managing products and inventory")
public class ProductController {
    
    private final ProductService productService;
    
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the inventory")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        log.info("REST request to get all products");
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found"),
        @ApiResponse(responseCode = "404", description = "Product not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        log.info("REST request to get product: {}", id);
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @Operation(summary = "Create new product", description = "Add a new product to the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input", 
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductCreateDTO createDTO) {
        log.info("REST request to create product: {}", createDTO.getName());
        ProductDTO createdProduct = productService.createProduct(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @Operation(summary = "Update product", description = "Update product information (excluding stock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO updateDTO) {
        log.info("REST request to update product: {}", id);
        ProductDTO updatedProduct = productService.updateProduct(id, updateDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @Operation(summary = "Delete product", description = "Remove a product from the inventory")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        log.info("REST request to delete product: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Add stock", description = "Increase the stock quantity of a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock added successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid quantity")
    })
    @PatchMapping("/{id}/stock/add")
    public ResponseEntity<ProductDTO> addStock(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO stockUpdateDTO) {
        log.info("REST request to add {} units to product: {}", 
            stockUpdateDTO.getQuantity(), id);
        ProductDTO updatedProduct = productService.addStock(id, stockUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @Operation(summary = "Remove stock", description = "Decrease the stock quantity of a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock removed successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Insufficient stock or invalid quantity")
    })
    @PatchMapping("/{id}/stock/remove")
    public ResponseEntity<ProductDTO> removeStock(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody StockUpdateDTO stockUpdateDTO) {
        log.info("REST request to remove {} units from product: {}", 
            stockUpdateDTO.getQuantity(), id);
        ProductDTO updatedProduct = productService.removeStock(id, stockUpdateDTO);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @Operation(summary = "Get low stock products", 
        description = "Retrieve all products that are below their low stock threshold")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock products")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        log.info("REST request to get products with low stock");
        List<ProductDTO> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }
    
    @Operation(summary = "Search products by name", 
        description = "Search for products containing the specified name")
    @ApiResponse(responseCode = "200", description = "Search completed successfully")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Product name to search") 
            @RequestParam(required = false) String name) {
        log.info("REST request to search products by name: {}", name);
        List<ProductDTO> products = productService.searchProductsByName(name != null ? name : "");
        return ResponseEntity.ok(products);
    }
}