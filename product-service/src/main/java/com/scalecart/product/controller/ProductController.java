package com.scalecart.product.controller;

import com.scalecart.product.dto.*;
import com.scalecart.product.entity.Product;
import com.scalecart.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product catalog management APIs")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // PUBLIC — anyone can browse products
    @GetMapping
    @Operation(
            summary = "Get all active products",
            description = "Returns paginated list of active products"
    )
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of products per page")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Search by product name")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by category ID")
            @RequestParam(required = false) Long categoryId) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Product> productPage;

        if (search != null && !search.isBlank()) {

            productPage = productService.searchProducts(
                    search,
                    pageable
            );

        } else if (categoryId != null) {

            productPage = productService.getProductsByCategory(
                    categoryId,
                    pageable
            );

        } else {

            productPage = productService.getAllProducts(
                    pageable
            );
        }

        return ResponseEntity.ok(
                ProductMapper.toPagedResponse(productPage)
        );
    }

    // PUBLIC — get single product by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        System.out.println(">>> CONTROLLER REACHED: " + id);

        Product product = productService.getProductById(id);

        System.out.println(">>> SERVICE RETURNED: " + product);

        ProductResponse response = ProductMapper.toResponse(product);

        System.out.println(">>> MAPPING COMPLETED");

        return ResponseEntity.ok(response);
    }

    // ADMIN only — create new product
    // Note: @PreAuthorize will be added on Day 18 when method security is wired
    @PostMapping
    @Operation(summary = "Create a new product (Admin only)")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product created = productService.createProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductMapper.toResponse(created));
    }

    // ADMIN only — update existing product
    @PutMapping("/{id}")
    @Operation(summary = "Update a product (Admin only)")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        Product updated = productService.updateProduct(
                id,
                request
        );

        return ResponseEntity.ok(
                ProductMapper.toResponse(updated)
        );
    }

    // ADMIN only — soft delete product
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product (Admin only)")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}