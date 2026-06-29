package com.scalecart.product.service;

import com.scalecart.product.entity.Product;
import com.scalecart.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Cache individual product by ID
    // Key = "product::42" in Redis
    // Second call for same ID: returns from Redis, zero DB query
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with id: " + id));
    }

    // Cache the product list page
    // Key = "products::PageRequest[page=0,size=10]"
    @Cacheable(value = "products", key = "#pageable")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    @Cacheable(value = "products-by-category",
            key = "#categoryId + '-' + #pageable")
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
    }

    @Cacheable(value = "products-search",
            key = "#name + '-' + #pageable")
    public Page<Product> searchProducts(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(
                name, pageable);
    }

    // When a product is saved/updated, evict ALL product caches
    // so next read gets fresh data from DB
    @CacheEvict(value = {"product", "products",
            "products-by-category", "products-search"},
            allEntries = true)
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // When a product is deleted (soft delete - just marks inactive),
    // also evict caches
    @CacheEvict(value = {"product", "products",
            "products-by-category", "products-search"},
            allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        product.setActive(false);    // soft delete - never actually DELETE rows
        productRepository.save(product);
    }
}