package com.scalecart.product.service;

import com.scalecart.product.AbstractIntegrationTest;
import com.scalecart.product.dto.ProductRequest;
import com.scalecart.product.entity.Product;
import com.scalecart.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@Transactional
@WithMockUser(roles = "ADMIN")
@DisplayName("ProductService Integration Tests — with real DB and Redis")
class ProductServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("Should create product and persist to real PostgreSQL")
    void createProduct_PersistsToDatabase() {
        ProductRequest request = new ProductRequest();
        request.setName("Test MacBook");
        request.setPrice(new BigDecimal("119999.00"));
        request.setStockQuantity(10);
        request.setCategoryId(1L);

        Product created = productService.createProduct(request);

        assertThat(created.getId()).isNotNull();

        Product fromDb = productRepository
                .findById(created.getId())
                .orElseThrow();

        assertThat(fromDb.getName()).isEqualTo("Test MacBook");
        assertThat(fromDb.getPrice())
                .isEqualByComparingTo(new BigDecimal("119999.00"));
        assertThat(fromDb.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should return paginated products from seeded data")
    void getAllProducts_ReturnsSeedData() {
        Page<Product> result = productService.getAllProducts(
                PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(3);
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("Should cache product on first call — verify with CacheManager")
    void getProductById_CachesResult() {
        Product firstCall = productService.getProductById(1L);
        assertThat(firstCall).isNotNull();

        Object cached = cacheManager.getCache("product")
                .get(1L)
                .get();

        assertThat(cached).isNotNull();
        assertThat(((Product) cached).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should evict cache on product update")
    void updateProduct_EvictsCache() {
        productService.getProductById(1L);
        assertThat(cacheManager.getCache("product").get(1L))
                .isNotNull();

        com.scalecart.product.dto.ProductUpdateRequest updateRequest =
                new com.scalecart.product.dto.ProductUpdateRequest();
        updateRequest.setName("Updated iPhone 15 Pro");
        productService.updateProduct(1L, updateRequest);

        assertThat(cacheManager.getCache("product").get(1L))
                .isNull();
    }

    @Test
    @DisplayName("Soft delete should mark product inactive not delete row")
    void deleteProduct_SoftDelete_RowStillExists() {
        productService.deleteProduct(1L);

        Product fromDb = productRepository
                .findById(1L)
                .orElseThrow();

        assertThat(fromDb.isActive()).isFalse();

        Page<Product> activeProducts = productService.getAllProducts(
                PageRequest.of(0, 10));
        assertThat(activeProducts.getContent())
                .noneMatch(p -> p.getId().equals(1L));
    }
}
