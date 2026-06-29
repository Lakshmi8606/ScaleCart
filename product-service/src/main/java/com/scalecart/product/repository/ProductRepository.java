package com.scalecart.product.repository;

import com.scalecart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all active products - paginated
    Page<Product> findByActiveTrue(Pageable pageable);

    // Get all active products in a category - paginated
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    // Search by name (case-insensitive contains)
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String name, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Optional<Product> findByIdAndActiveTrue(Long id);
}