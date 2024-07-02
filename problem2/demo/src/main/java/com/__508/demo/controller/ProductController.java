package com.__508.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.__508.demo.service.ProductService;

import com.__508.demo.model.Product;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{categoryName}/products")
    public List<Product> getTopProducts(
            @PathVariable String categoryName,
            @RequestParam int n,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return productService.getTopProducts(categoryName, n, page, sortBy, sortDirection, minPrice, maxPrice);
    }

    @GetMapping("/{categoryName}/products/{productId}")
    public Product getProductById(
            @PathVariable String categoryName,
            @PathVariable String productId
    ) {
        return productService.getProductById(categoryName, productId);
    }
}
