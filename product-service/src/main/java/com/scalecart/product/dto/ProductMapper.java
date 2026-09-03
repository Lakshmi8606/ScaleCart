package com.scalecart.product.dto;

import com.scalecart.product.entity.Product;
import org.springframework.data.domain.Page;

public class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        return response;
    }

    public static PagedResponse<ProductResponse> toPagedResponse(Page<Product> page) {
        return new PagedResponse<>(
                page.getContent().stream()
                        .map(ProductMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}