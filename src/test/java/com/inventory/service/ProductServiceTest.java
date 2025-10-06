package com.inventory.service;

import com.inventory.dto.*;
import com.inventory.entity.Product;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.InvalidStockOperationException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.mapper.ProductMapper;
import com.inventory.repository.ProductRepository;
import com.inventory.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests")
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;
    
    @InjectMocks
    private ProductServiceImpl productService;
    
    private Product testProduct;
    private ProductDTO testProductDTO;
    private ProductCreateDTO createDTO;
    private ProductUpdateDTO updateDTO;
    private StockUpdateDTO stockUpdateDTO;
    
    @BeforeEach
    void setUp() {
        // Initialize test data
        testProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .stockQuantity(50)
            .lowStockThreshold(10)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        testProductDTO = ProductDTO.builder()
            .id(1L)
            .name("Test Product")
            .description("Test Description")
            .stockQuantity(50)
            .lowStockThreshold(10)
            .isLowStock(false)
            .build();
        
        createDTO = ProductCreateDTO.builder()
            .name("New Product")
            .description("New Description")
            .stockQuantity(100)
            .lowStockThreshold(15)
            .build();
        
        updateDTO = ProductUpdateDTO.builder()
            .name("Updated Product")
            .description("Updated Description")
            .lowStockThreshold(20)
            .build();
        
        stockUpdateDTO = StockUpdateDTO.builder()
            .quantity(10)
            .build();
    }
    
    // ==================== GET ALL PRODUCTS TESTS ====================
    
    @Test
    @DisplayName("Should get all products successfully")
    void getAllProducts_Success() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        List<ProductDTO> result = productService.getAllProducts();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findAll();
    }
    
    // ==================== GET PRODUCT BY ID TESTS ====================
    
    @Test
    @DisplayName("Should get product by ID successfully")
    void getProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.getProductById(1L);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when product not found")
    void getProductById_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productService.getProductById(999L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product with ID 999 not found");
        
        verify(productRepository, times(1)).findById(999L);
    }
    
    // ==================== CREATE PRODUCT TESTS ====================
    
    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        // Given
        Product newProduct = Product.builder()
            .name("New Product")
            .description("New Description")
            .stockQuantity(100)
            .lowStockThreshold(15)
            .build();
        
        Product savedProduct = Product.builder()
            .id(2L)
            .name("New Product")
            .description("New Description")
            .stockQuantity(100)
            .lowStockThreshold(15)
            .build();
        
        when(productRepository.existsByNameIgnoreCase("New Product")).thenReturn(false);
        when(productMapper.toEntity(createDTO)).thenReturn(newProduct);
        when(productRepository.save(newProduct)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.createProduct(createDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).existsByNameIgnoreCase("New Product");
        verify(productRepository, times(1)).save(newProduct);
    }
    
    @Test
    @DisplayName("Should throw exception when product name already exists")
    void createProduct_DuplicateName() {
        // Given
        when(productRepository.existsByNameIgnoreCase("New Product")).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> productService.createProduct(createDTO))
            .isInstanceOf(InvalidStockOperationException.class)
            .hasMessageContaining("Product with name 'New Product' already exists");
        
        verify(productRepository, never()).save(any());
    }
    
    // ==================== STOCK ADDITION TESTS ====================
    
    @Test
    @DisplayName("Should add stock successfully")
    void addStock_Success() {
        // Given
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.addStock(1L, stockUpdateDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findByIdWithLock(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        assertThat(testProduct.getStockQuantity()).isEqualTo(60); // 50 + 10
    }
    
    @Test
    @DisplayName("Should throw exception when adding stock causes overflow")
    void addStock_Overflow() {
        // Given
        testProduct.setStockQuantity(Integer.MAX_VALUE - 5);
        stockUpdateDTO.setQuantity(10);
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.addStock(1L, stockUpdateDTO))
            .isInstanceOf(InvalidStockOperationException.class)
            .hasMessageContaining("Stock addition would exceed maximum allowed value");
        
        verify(productRepository, never()).save(any());
    }
    
    // ==================== STOCK REMOVAL TESTS ====================
    
    @Test
    @DisplayName("Should remove stock successfully")
    void removeStock_Success() {
        // Given
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.removeStock(1L, stockUpdateDTO);
        
        // Then
        assertThat(result).isNotNull();
        verify(productRepository, times(1)).findByIdWithLock(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        assertThat(testProduct.getStockQuantity()).isEqualTo(40); // 50 - 10
    }
    
    @Test
    @DisplayName("Should throw exception when removing more stock than available")
    void removeStock_InsufficientStock() {
        // Given
        stockUpdateDTO.setQuantity(60); // More than available (50)
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        assertThatThrownBy(() -> productService.removeStock(1L, stockUpdateDTO))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock for product ID 1")
            .hasMessageContaining("Requested: 60, Available: 50");
        
        verify(productRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should remove stock to exactly zero")
    void removeStock_ToZero() {
        // Given
        stockUpdateDTO.setQuantity(50); // Exactly what's available
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.removeStock(1L, stockUpdateDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(0);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Should trigger low stock warning when stock goes below threshold")
    void removeStock_LowStockWarning() {
        // Given
        testProduct.setStockQuantity(15);
        testProduct.setLowStockThreshold(10);
        stockUpdateDTO.setQuantity(8); // Will bring it to 7, below threshold of 10
        
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.removeStock(1L, stockUpdateDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(testProduct.getStockQuantity()).isEqualTo(7);
        assertThat(testProduct.isLowStock()).isTrue();
    }
    
    // ==================== LOW STOCK PRODUCTS TESTS ====================
    
    @Test
    @DisplayName("Should get low stock products successfully")
    void getLowStockProducts_Success() {
        // Given
        Product lowStockProduct = Product.builder()
            .id(2L)
            .name("Low Stock Product")
            .stockQuantity(5)
            .lowStockThreshold(10)
            .build();
        
        List<Product> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productRepository.findLowStockProducts()).thenReturn(lowStockProducts);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        List<ProductDTO> result = productService.getLowStockProducts();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findLowStockProducts();
    }
    
    // ==================== UPDATE PRODUCT TESTS ====================
    
    @Test
    @DisplayName("Should update product successfully")
    void updateProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsByNameIgnoreCase("Updated Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.updateProduct(1L, updateDTO);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(testProduct.getName()).isEqualTo("Updated Product");
        assertThat(testProduct.getDescription()).isEqualTo("Updated Description");
        assertThat(testProduct.getLowStockThreshold()).isEqualTo(20);
        verify(productRepository, times(1)).save(testProduct);
    }
    
    @Test
    @DisplayName("Should update product with partial data")
    void updateProduct_PartialUpdate() {
        // Given
        ProductUpdateDTO partialUpdate = ProductUpdateDTO.builder()
            .description("Only Description Updated")
            .build();
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);
        
        // When
        ProductDTO result = productService.updateProduct(1L, partialUpdate);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(testProduct.getName()).isEqualTo("Test Product"); // Unchanged
        assertThat(testProduct.getDescription()).isEqualTo("Only Description Updated");
        verify(productRepository, times(1)).save(testProduct);
    }
    
    // ==================== DELETE PRODUCT TESTS ====================
    
    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        
        // When
        productService.deleteProduct(1L);
        
        // Then
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> productService.deleteProduct(999L))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("Product with ID 999 not found");
        
        verify(productRepository, never()).deleteById(any());
    }
}