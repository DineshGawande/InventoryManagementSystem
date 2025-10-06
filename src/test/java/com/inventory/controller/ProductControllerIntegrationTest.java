package com.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.dto.ProductCreateDTO;
import com.inventory.dto.StockUpdateDTO;
import com.inventory.entity.Product;
import com.inventory.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Controller Integration Tests")
class ProductControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ProductRepository productRepository;
    
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        productRepository.deleteAll();
        
        // Create test product
        testProduct = Product.builder()
            .name("Integration Test Product")
            .description("Product for integration testing")
            .stockQuantity(100)
            .lowStockThreshold(20)
            .build();
        testProduct = productRepository.save(testProduct);
    }
    
    // ==================== GET ENDPOINTS TESTS ====================
    
    @Test
    @Order(1)
    @DisplayName("Should get all products")
    void getAllProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Integration Test Product"))
            .andExpect(jsonPath("$[0].stockQuantity").value(100));
    }
    
    @Test
    @Order(2)
    @DisplayName("Should get product by ID")
    void getProductById() throws Exception {
        mockMvc.perform(get("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testProduct.getId()))
            .andExpect(jsonPath("$.name").value("Integration Test Product"))
            .andExpect(jsonPath("$.stockQuantity").value(100))
            .andExpect(jsonPath("$.isLowStock").value(false));
    }
    
    @Test
    @Order(3)
    @DisplayName("Should return 404 for non-existent product")
    void getProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").exists());
    }
    
    // ==================== CREATE PRODUCT TESTS ====================
    
    @Test
    @Order(4)
    @DisplayName("Should create new product")
    @Transactional
    void createProduct() throws Exception {
        ProductCreateDTO createDTO = ProductCreateDTO.builder()
            .name("New Product")
            .description("New product description")
            .stockQuantity(50)
            .lowStockThreshold(10)
            .build();
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Product"))
            .andExpect(jsonPath("$.stockQuantity").value(50))
            .andExpect(jsonPath("$.lowStockThreshold").value(10));
    }
    
    @Test
    @Order(5)
    @DisplayName("Should return 400 for invalid product creation")
    void createProduct_InvalidInput() throws Exception {
        ProductCreateDTO invalidDTO = ProductCreateDTO.builder()
            .name("") // Invalid: empty name
            .stockQuantity(-10) // Invalid: negative stock
            .build();
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors").exists())
            .andExpect(jsonPath("$.validationErrors.name").exists())
            .andExpect(jsonPath("$.validationErrors.stockQuantity").exists());
    }
    
    // ==================== STOCK MANAGEMENT TESTS ====================
    
    @Test
    @Order(6)
    @DisplayName("Should add stock successfully")
    @Transactional
    void addStock() throws Exception {
        StockUpdateDTO stockUpdate = StockUpdateDTO.builder()
            .quantity(25)
            .build();
        
        mockMvc.perform(patch("/api/products/{id}/stock/add", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stockQuantity").value(125)); // 100 + 25
    }
    
    @Test
    @Order(7)
    @DisplayName("Should remove stock successfully")
    @Transactional
    void removeStock() throws Exception {
        StockUpdateDTO stockUpdate = StockUpdateDTO.builder()
            .quantity(30)
            .build();
        
        mockMvc.perform(patch("/api/products/{id}/stock/remove", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stockQuantity").value(70)); // 100 - 30
    }
    
    @Test
    @Order(8)
    @DisplayName("Should return 400 for insufficient stock")
    void removeStock_InsufficientStock() throws Exception {
        StockUpdateDTO stockUpdate = StockUpdateDTO.builder()
            .quantity(150) // More than available (100)
            .build();
        
        mockMvc.perform(patch("/api/products/{id}/stock/remove", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockUpdate)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(containsString("Insufficient stock")));
    }
    
    @Test
    @Order(9)
    @DisplayName("Should return 400 for invalid stock quantity")
    void addStock_InvalidQuantity() throws Exception {
        StockUpdateDTO invalidStock = StockUpdateDTO.builder()
            .quantity(0) // Invalid: must be at least 1
            .build();
        
        mockMvc.perform(patch("/api/products/{id}/stock/add", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidStock)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.validationErrors.quantity").exists());
    }
    
    // ==================== LOW STOCK TESTS ====================
    
    @Test
    @Order(10)
    @DisplayName("Should get low stock products")
    @Transactional
    void getLowStockProducts() throws Exception {
        // Create a low stock product
        Product lowStockProduct = Product.builder()
            .name("Low Stock Item")
            .description("This item is low on stock")
            .stockQuantity(5)
            .lowStockThreshold(10)
            .build();
        productRepository.save(lowStockProduct);
        
        mockMvc.perform(get("/api/products/low-stock")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("Low Stock Item"))
            .andExpect(jsonPath("$[0].isLowStock").value(true));
    }
    
    // ==================== UPDATE PRODUCT TESTS ====================
    
    @Test
    @Order(11)
    @DisplayName("Should update product successfully")
    @Transactional
    void updateProduct() throws Exception {
        String updateJson = """
            {
                "name": "Updated Product Name",
                "description": "Updated Description",
                "lowStockThreshold": 25
            }
            """;
        
        mockMvc.perform(put("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Product Name"))
            .andExpect(jsonPath("$.description").value("Updated Description"))
            .andExpect(jsonPath("$.lowStockThreshold").value(25))
            .andExpect(jsonPath("$.stockQuantity").value(100)); // Stock unchanged
    }
    
    // ==================== DELETE PRODUCT TESTS ====================
    
    @Test
    @Order(12)
    @DisplayName("Should delete product successfully")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNoContent());
        
        // Verify deletion
        mockMvc.perform(get("/api/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    @Order(13)
    @DisplayName("Should return 404 when deleting non-existent product")
    void deleteProduct_NotFound() throws Exception {
        mockMvc.perform(delete("/api/products/9999")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
    
    // ==================== SEARCH TESTS ====================
    
    @Test
    @Order(14)
    @DisplayName("Should search products by name")
    @Transactional
    void searchProducts() throws Exception {
        // Create additional products for searching
        productRepository.save(Product.builder()
            .name("Laptop Computer")
            .description("High-end laptop")
            .stockQuantity(10)
            .build());
        
        productRepository.save(Product.builder()
            .name("Desktop Computer")
            .description("Desktop PC")
            .stockQuantity(5)
            .build());
        
        mockMvc.perform(get("/api/products/search")
                .param("name", "Computer")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].name", everyItem(containsString("Computer"))));
    }
}