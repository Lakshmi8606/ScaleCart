package com.scalecart.product;

import com.scalecart.product.entity.Product;
import com.scalecart.product.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestCacheController {

    private final ProductService productService;

    public TestCacheController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/test/product/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        System.out.println("Controller called for product: " + id);
        return productService.getProductById(id);
    }
}