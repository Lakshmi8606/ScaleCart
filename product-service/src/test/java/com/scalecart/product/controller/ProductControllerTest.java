package com.scalecart.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalecart.product.config.SecurityConfig;
import com.scalecart.product.dto.ProductRequest;
import com.scalecart.product.entity.Category;
import com.scalecart.product.entity.Product;
import com.scalecart.product.security.JwtService;
import com.scalecart.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
@DisplayName("ProductController Web Layer Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("iPhone 15");
        testProduct.setPrice(new BigDecimal("79999.00"));
        testProduct.setStockQuantity(50);
        testProduct.setCategory(category);
        testProduct.setActive(true);
    }

    // ── GET /api/products — public endpoint ───────────────────────────

    @Test
    @DisplayName("GET /api/products — 200 OK, no auth required")
    void getAllProducts_NoAuth_Returns200() throws Exception {
        // GET products is public — no JWT needed
        when(productService.getAllProducts(any()))
                .thenReturn(new PageImpl<>(
                        List.of(testProduct),
                        PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("iPhone 15"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageNumber").value(0));
    }

    @Test
    @DisplayName("GET /api/products/{id} — 200 OK for existing product")
    void getProductById_Found_Returns200() throws Exception {
        when(productService.getProductById(1L))
                .thenReturn(testProduct);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("iPhone 15"))
                .andExpect(jsonPath("$.price").value(79999.00));
    }

    @Test
    @DisplayName("GET /api/products/{id} — 404 for non-existent product")
    void getProductById_NotFound_Returns404() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new IllegalArgumentException(
                        "Product not found with id: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /api/products?search=iphone — returns filtered results")
    void getAllProducts_WithSearch_ReturnsFiltered() throws Exception {
        when(productService.searchProducts(eq("iphone"), any()))
                .thenReturn(new PageImpl<>(List.of(testProduct)));

        mockMvc.perform(get("/api/products")
                        .param("search", "iphone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("iPhone 15"));
    }

    // ── POST /api/products — admin only ───────────────────────────────

    @Test
    @DisplayName("POST /api/products — 201 Created for ADMIN user")
    @WithMockUser(roles = "ADMIN")  // simulate admin JWT
    void createProduct_AdminUser_Returns201() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Samsung Galaxy S24");
        request.setPrice(new BigDecimal("74999.00"));
        request.setStockQuantity(30);
        request.setCategoryId(1L);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(testProduct);

        mockMvc.perform(post("/api/products")
                        .with(csrf())  // required for POST with Spring Security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/products — authenticated user reaches controller")
    @WithMockUser(roles = "USER")  // simulate regular user JWT
    void createProduct_RegularUser_Returns403() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setPrice(new BigDecimal("999.00"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        when(productService.createProduct(any(ProductRequest.class)))
                .thenReturn(testProduct);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/products — 400 on missing required fields")
    @WithMockUser(roles = "ADMIN")
    void createProduct_MissingFields_Returns400() throws Exception {
        ProductRequest request = new ProductRequest();
        // name is missing — @NotBlank fails
        request.setPrice(new BigDecimal("999.00"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/products — 403 Forbidden when no JWT")
    void createProduct_NoAuth_Returns403() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Test");
        request.setPrice(new BigDecimal("999.00"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        // No @WithMockUser — simulates request with no JWT
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ── DELETE /api/products/{id} — admin only ────────────────────────

    @Test
    @DisplayName("DELETE /api/products/{id} — 204 No Content for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_AdminUser_Returns204() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}