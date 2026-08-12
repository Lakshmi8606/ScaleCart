package com.scalecart.product.service;

import com.scalecart.product.dto.ProductRequest;
import com.scalecart.product.dto.ProductUpdateRequest;
import com.scalecart.product.entity.Category;
import com.scalecart.product.entity.Product;
import com.scalecart.product.repository.CategoryRepository;
import com.scalecart.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.scalecart.product.annotation.TrackExecutionTime;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Cache individual product by ID
    @TrackExecutionTime
    @Cacheable(value = "product", key = "#id")
    public Product getProductById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with id: " + id));
    }

    // Cache all active products
    @Cacheable(value = "products", key = "#pageable")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    // Cache products by category
    @Cacheable(value = "products-by-category",
            key = "#categoryId + '-' + #pageable")
    public Page<Product> getProductsByCategory(Long categoryId,
                                               Pageable pageable) {
        return productRepository.findByCategoryIdAndActiveTrue(
                categoryId, pageable);
    }

    // Cache search results
    @Cacheable(value = "products-search",
            key = "#name + '-' + #pageable")
    public Page<Product> searchProducts(String name,
                                        Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCaseAndActiveTrue(
                        name, pageable);
    }

    // Create Product
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {
            "product",
            "products",
            "products-by-category",
            "products-search"
    }, allEntries = true)
    @Transactional
    public Product createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: "
                                + request.getCategoryId()));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());

        return productRepository.save(product);
    }

    // Update Product
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {
            "product",
            "products",
            "products-by-category",
            "products-search"
    }, allEntries = true)
    @Transactional
    public Product updateProduct(Long id,
                                 ProductUpdateRequest request) {

        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with id: " + id));

        if (request.getName() != null)
            product.setName(request.getName());

        if (request.getDescription() != null)
            product.setDescription(request.getDescription());

        if (request.getPrice() != null)
            product.setPrice(request.getPrice());

        if (request.getStockQuantity() != null)
            product.setStockQuantity(request.getStockQuantity());

        if (request.getImageUrl() != null)
            product.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {

            Category category = categoryRepository.findById(
                            request.getCategoryId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found with id: "
                                            + request.getCategoryId()));

            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    // Save Product
    @CacheEvict(value = {
            "product",
            "products",
            "products-by-category",
            "products-search"
    }, allEntries = true)
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Soft Delete Product
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = {
            "product",
            "products",
            "products-by-category",
            "products-search"
    }, allEntries = true)
    @Transactional
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        product.setActive(false);

        productRepository.save(product);
    }
}