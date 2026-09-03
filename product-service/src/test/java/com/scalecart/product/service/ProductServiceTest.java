package com.scalecart.product.service;

import com.scalecart.product.dto.ProductRequest;
import com.scalecart.product.entity.Category;
import com.scalecart.product.entity.Product;
import com.scalecart.product.repository.CategoryRepository;
import com.scalecart.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("iPhone 15");
        testProduct.setPrice(new BigDecimal("79999.00"));
        testProduct.setStockQuantity(50);
        testProduct.setCategory(testCategory);
        testProduct.setActive(true);
    }

    @Test
    @DisplayName("Should return product when found by ID")
    void getProductById_Found() {
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("iPhone 15");
        assertThat(result.getPrice())
                .isEqualByComparingTo(new BigDecimal("79999.00"));

        verify(productRepository).findByIdAndActiveTrue(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product not found")
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findByIdAndActiveTrue(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found with id: 999");
    }

    @Test
    @DisplayName("Should return paged products correctly")
    void getAllProducts_ReturnsPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> mockPage = new PageImpl<>(
                List.of(testProduct),
                pageable,
                1L
        );

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getName())
                .isEqualTo("iPhone 15");
    }

    @Test
    @DisplayName("Should return empty page when no products exist")
    void getAllProducts_EmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(
                List.of(), pageable, 0L);

        when(productRepository.findByActiveTrue(pageable))
                .thenReturn(emptyPage);

        Page<Product> result = productService.getAllProducts(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should create product successfully with valid request")
    void createProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Samsung Galaxy S24");
        request.setPrice(new BigDecimal("74999.00"));
        request.setStockQuantity(30);
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> {
                    Product p = inv.getArgument(0);
                    p.setId(2L);
                    return p;
                });

        Product result = productService.createProduct(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Samsung Galaxy S24");
        assertThat(result.getPrice())
                .isEqualByComparingTo(new BigDecimal("74999.00"));
        assertThat(result.getCategory().getName()).isEqualTo("Electronics");

        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when category not found during product creation")
    void createProduct_CategoryNotFound_ThrowsException() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setPrice(new BigDecimal("999.00"));
        request.setStockQuantity(10);
        request.setCategoryId(999L);

        when(categoryRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found with id: 999");

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should soft delete product by setting active=false")
    void deleteProduct_SetsActiveFalse() {
        when(productRepository.findByIdAndActiveTrue(1L))
                .thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(testProduct);

        productService.deleteProduct(1L);

        assertThat(testProduct.isActive()).isFalse();

        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when trying to delete non-existent product")
    void deleteProduct_NotFound_ThrowsException() {
        when(productRepository.findByIdAndActiveTrue(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");
    }
}
