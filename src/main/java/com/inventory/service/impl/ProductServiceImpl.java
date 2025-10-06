package com.inventory.service.impl;

import com.inventory.dto.*;
import com.inventory.entity.Product;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidStockOperationException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.mapper.ProductMapper;
import com.inventory.repository.ProductRepository;
import com.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Product operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    
    @Override
    public List<ProductDTO> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDTO(product);
    }
    
    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO createDTO) {
        log.debug("Creating new product: {}", createDTO.getName());
        
        // Check if product with same name already exists
        if (productRepository.existsByNameIgnoreCase(createDTO.getName())) {
            throw new InvalidStockOperationException(
                "Product with name '" + createDTO.getName() + "' already exists");
        }
        
        Product product = productMapper.toEntity(createDTO);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }
    
    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO updateDTO) {
        log.debug("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        // Update only non-null fields
        if (updateDTO.getName() != null) {
            // Check if another product with the same name exists
            if (productRepository.existsByNameIgnoreCase(updateDTO.getName()) &&
                !product.getName().equalsIgnoreCase(updateDTO.getName())) {
                throw new InvalidStockOperationException(
                    "Product with name '" + updateDTO.getName() + "' already exists");
            }
            product.setName(updateDTO.getName());
        }
        
        if (updateDTO.getDescription() != null) {
            product.setDescription(updateDTO.getDescription());
        }
        
        if (updateDTO.getLowStockThreshold() != null) {
            product.setLowStockThreshold(updateDTO.getLowStockThreshold());
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", id);
        return productMapper.toDTO(updatedProduct);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional
    public ProductDTO addStock(Long productId, StockUpdateDTO stockUpdateDTO) {
        log.debug("Adding {} units to product ID: {}", stockUpdateDTO.getQuantity(), productId);
        
        // Use pessimistic locking to prevent concurrent modifications
        Product product = productRepository.findByIdWithLock(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        
        int quantityToAdd = stockUpdateDTO.getQuantity();
        
        // Validate the operation won't cause overflow
        if (product.getStockQuantity() > Integer.MAX_VALUE - quantityToAdd) {
            throw new InvalidStockOperationException(
                "Stock addition would exceed maximum allowed value");
        }
        
        product.addStock(quantityToAdd);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Successfully added {} units to product ID: {}. New stock: {}", 
            quantityToAdd, productId, updatedProduct.getStockQuantity());
        
        return productMapper.toDTO(updatedProduct);
    }
    
    @Override
    @Transactional
    public ProductDTO removeStock(Long productId, StockUpdateDTO stockUpdateDTO) {
        log.debug("Removing {} units from product ID: {}", stockUpdateDTO.getQuantity(), productId);
        
        // Use pessimistic locking to prevent concurrent modifications
        Product product = productRepository.findByIdWithLock(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        
        int quantityToRemove = stockUpdateDTO.getQuantity();
        
        // Check if sufficient stock is available
        if (product.getStockQuantity() < quantityToRemove) {
            throw new InsufficientStockException(
                productId, 
                quantityToRemove, 
                product.getStockQuantity()
            );
        }
        
        product.removeStock(quantityToRemove);
        Product updatedProduct = productRepository.save(product);
        
        log.info("Successfully removed {} units from product ID: {}. New stock: {}", 
            quantityToRemove, productId, updatedProduct.getStockQuantity());
        
        // Check if low stock alert should be triggered
        if (updatedProduct.isLowStock()) {
            log.warn("Product ID: {} is now low on stock. Current: {}, Threshold: {}", 
                productId, 
                updatedProduct.getStockQuantity(), 
                updatedProduct.getLowStockThreshold());
        }
        
        return productMapper.toDTO(updatedProduct);
    }
    
    @Override
    public List<ProductDTO> getLowStockProducts() {
        log.debug("Fetching products with low stock");
        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        
        log.info("Found {} products with low stock", lowStockProducts.size());
        return lowStockProducts.stream()
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductDTO> searchProductsByName(String name) {
        log.debug("Searching products by name: {}", name);
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream()
            .map(productMapper::toDTO)
            .collect(Collectors.toList());
    }
}